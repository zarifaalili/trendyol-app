package org.example.trendyolfinalproject.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.trendyolfinalproject.dao.entity.*;
import org.example.trendyolfinalproject.dao.repository.*;
import org.example.trendyolfinalproject.exception.customExceptions.AlreadyException;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.CollectionItemMapper;
import org.example.trendyolfinalproject.mapper.CollectionMapper;
import org.example.trendyolfinalproject.model.request.CollectionCreateRequest;
import org.example.trendyolfinalproject.model.request.CollectionItemFromWishListRequest;
import org.example.trendyolfinalproject.model.request.CollectionItemRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.CollectionItemResponse;
import org.example.trendyolfinalproject.model.response.CollectionResponse;
import org.example.trendyolfinalproject.service.impl.CollectionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CollectionServiceImplTest {

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CollectionMapper collectionMapper;

    @Mock
    private CollectionItemRepository collectionItemRepository;

    @Mock
    private CollectionItemMapper collectionItemMapper;

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CollectionServiceImpl collectionService;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        when(attributes.getRequest()).thenReturn(request);
        when(request.getAttribute("userId")).thenReturn(1L);
        RequestContextHolder.setRequestAttributes(attributes);
    }


    @Test
    void testCreateCollection_Success() {
        CollectionCreateRequest req = new CollectionCreateRequest("My Collection");

        User user = new User();
        user.setId(1L);

        Collection collection = new Collection();
        collection.setId(1L);
        collection.setUser(user);
        collection.setName(req.getName());

        CollectionResponse response = CollectionResponse.builder().id(1L).name(req.getName()).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(collectionRepository.findByUser_IdAndName(user.getId(), req.getName())).thenReturn(Optional.empty());
        when(collectionMapper.toEntity(req)).thenReturn(collection);
        when(collectionRepository.save(collection)).thenReturn(collection);
        when(collectionMapper.toResponse(collection)).thenReturn(response);

        ApiResponse<CollectionResponse> apiResponse = collectionService.createCollection(req);

        assertEquals(200, apiResponse.getStatus());
        assertEquals("My Collection", apiResponse.getData().getName());
    }


    @Test
    void testAddProductToCollection_Success() {
        CollectionItemRequest req = new CollectionItemRequest(1L, 10L);

        User user = new User();
        user.setId(1L);

        Collection collection = new Collection();
        collection.setId(1L);
        collection.setUser(user);

        ProductVariant variant = new ProductVariant();
        variant.setId(10L);
        Product product = new Product();
        product.setName("Test Product");
        variant.setProduct(product);

        CollectionItem entity = new CollectionItem();
        entity.setId(100L);

        CollectionItemResponse itemResponse = new CollectionItemResponse(100L, 10L, "Test Product", LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));
        when(collectionItemRepository.findByProductVariant_IdAndCollection_Id(10L, 1L)).thenReturn(Optional.empty());
        when(productVariantRepository.findById(10L)).thenReturn(Optional.of(variant));
        when(collectionItemMapper.toEntity(req)).thenReturn(entity);
        when(collectionItemRepository.save(entity)).thenReturn(entity);
        when(collectionItemMapper.toResponse(entity)).thenReturn(itemResponse);

        ApiResponse<CollectionItemResponse> apiResponse = collectionService.addProductToCollection(req);

        assertEquals(200, apiResponse.getStatus());
        assertEquals(10L, apiResponse.getData().getProductVariantId());
    }




    @Test
    void testGetAllCollections_Success() {
        User user = new User();
        user.setId(1L);

        Collection collection = new Collection();
        collection.setId(1L);
        collection.setUser(user);
        collection.setName("My Collection");
        collection.setCreatedAt(LocalDateTime.now());
        collection.setUpdatedAt(LocalDateTime.now());
        collection.setViewCount(0L);

        CollectionItem item = new CollectionItem();
        ProductVariant variant = new ProductVariant();
        variant.setId(10L);
        Product product = new Product();
        product.setName("Test Product");
        variant.setProduct(product);
        item.setProductVariant(variant);
        item.setId(100L);
        item.setCollection(collection);

        when(collectionRepository.findByUser_Id(1L)).thenReturn(List.of(collection));
        when(collectionItemRepository.findByCollection_Id(1L)).thenReturn(List.of(item));

        ApiResponse<List<CollectionResponse>> apiResponse = collectionService.getAllCollections();

        assertEquals(200, apiResponse.getStatus());
        assertEquals(1, apiResponse.getData().size());
        assertEquals("My Collection", apiResponse.getData().get(0).getName());
    }


    @Test
    void testShareCollection_Success() {
        User owner = new User();
        owner.setId(1L);

        User target = new User();
        target.setId(2L);

        Collection collection = new Collection();
        collection.setId(1L);
        collection.setUser(owner);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));

        ApiResponse<Void> apiResponse = collectionService.shareCollection(1L, 2L);

        assertEquals(200, apiResponse.getStatus());
        assertTrue(collection.getSharedWith().contains(target));
    }


    @Test
    void testReadSharedCollection_Success() {
        User owner = new User();
        owner.setId(1L);

        User sharedUser = new User();
        sharedUser.setId(2L);

        Collection collection = new Collection();
        collection.setId(1L);
        collection.setUser(owner);
        collection.getSharedWith().add(sharedUser);
        collection.setViewCount(0L);

        CollectionItem item = new CollectionItem();
        ProductVariant variant = new ProductVariant();
        variant.setId(10L);
        Product product = new Product();
        product.setName("Test Product");
        variant.setProduct(product);
        item.setProductVariant(variant);
        item.setId(100L);
        item.setCollection(collection);

        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));
        when(collectionItemRepository.findByCollection_Id(1L)).thenReturn(List.of(item));

        ApiResponse<CollectionResponse> apiResponse = collectionService.readSharedCollection(1L, 2L);

        assertEquals(200, apiResponse.getStatus());
        assertEquals(1L, apiResponse.getData().getId());
        assertEquals(1, apiResponse.getData().getItems().size());
        assertEquals(1, collection.getViewCount()); // viewCount artmalı
    }


    @Test
    void testRenameCollection_Success() {
        Collection collection = new Collection();
        collection.setId(1L);
        collection.setUser(new User() {{
            setId(1L);
        }});
        collection.setName("Old Name");

        CollectionResponse response = new CollectionResponse();
        response.setId(1L);
        response.setName("New Name");

        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));
        when(collectionMapper.toResponse(collection)).thenReturn(response);

        ApiResponse<CollectionResponse> apiResponse = collectionService.renameCollection(1L, "New Name");

        assertEquals(200, apiResponse.getStatus());
        assertEquals("New Name", apiResponse.getData().getName());
        assertEquals("New Name", collection.getName()); // entity update olunub
    }

    @Test
    void testCreateCollection_AlreadyException() {
        CollectionCreateRequest request = new CollectionCreateRequest("My Collection");

        var user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(collectionRepository.findByUser_IdAndName(1L, "My Collection")).thenReturn(Optional.of(new Collection()));

        assertThrows(AlreadyException.class, () -> collectionService.createCollection(request));
    }

    @Test
    void testAddProductToCollection_CollectionNotFound() {
        CollectionItemRequest request = new CollectionItemRequest(1L, 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(collectionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> collectionService.addProductToCollection(request));
    }

    @Test
    void testAddProductToCollection_AlreadyException() {
        CollectionItemRequest request = new CollectionItemRequest(1L, 1L);
        var collection = new Collection();
        collection.setUser(new User(){{
            setId(1L);
        }});

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(){{
            setId(1L);
        }}));
        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));
        when(collectionItemRepository.findByProductVariant_IdAndCollection_Id(1L, 1L)).thenReturn(Optional.of(new CollectionItem()));

        assertThrows(AlreadyException.class, () -> collectionService.addProductToCollection(request));
    }

    @Test
    void testAddProductToCollectionFromWishList_UserNotFound() {
        CollectionItemFromWishListRequest request = new CollectionItemFromWishListRequest(1L, 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> collectionService.addProductToCollectionFromWishList(request));
    }


    @Test
    void testReadSharedCollection_NoPermission() {
        var collection = new Collection();
        collection.setUser(new User(){{
            setId(1L);
        }});
        collection.setSharedWith(List.of()); // heç kimlə paylaşılmayıb

        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));

        assertThrows(RuntimeException.class, () -> collectionService.readSharedCollection(1L, 2L));
    }

    @Test
    void testRenameCollection_NoPermission() {
        var collection = new Collection();
        collection.setUser(new User(){{
            setId(2L);
        }});
        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));

        assertThrows(RuntimeException.class, () -> collectionService.renameCollection(1L, "New Name"));
    }

    @Test
    void testRenameCollection_NotFound() {
        when(collectionRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> collectionService.renameCollection(1L, "New Name"));
    }
}

