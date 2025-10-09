//package mapper
//
//import org.example.trendyolfinalproject.dao.entity.User
//import org.example.trendyolfinalproject.mapper.UserMapper
//import org.example.trendyolfinalproject.model.request.UserRegisterRequest
//import org.example.trendyolfinalproject.model.response.UserProfileResponse
//import org.example.trendyolfinalproject.model.response.UserResponse
//
//import java.time.LocalDateTime
//
//
//class UserMapperSpec extends Specification {
//
//    UserMapper userMapper = Mappers.getMapper(UserMapper.class)
//
//    def "toEntity should map UserRegisterRequest to User"() {
//        given:
//        def request = new UserRegisterRequest()
//        request.setName("Zari")
//        request.setSurname("Əlili")
//        request.setEmail("zari@example.com")
//
//        when:
//        User user = userMapper.toEntity(request)
//
//        then:
//        user != null
//        user.name == "Zari"
//        user.surname == "Əlili"
//        user.email == "zari@example.com"
//        user.isActive
//        user.id == null
//        user.passwordHash == null
//        user.createdAt instanceof LocalDateTime
//        user.updatedAt instanceof LocalDateTime
//    }
//
//    def "toUserProfileResponse should map User to UserProfileResponse correctly"() {
//        given:
//        def user = new User()
//        user.setName("Zari")
//        user.setSurname("Əlili")
//        user.setEmail("zari@example.com")
//
//        when:
//        UserProfileResponse response = userMapper.toUserProfileResponse(user)
//
//        then:
//        response != null
//        response.fullName == "Zari Əlili"
//        response.username == "zari@example.com"
//        response.addresses == null
//        response.defaultPaymentMethod == null
//    }
//
//    def "toUserResponse should map User to UserResponse"() {
//        given:
//        def user = new User()
//        user.setId(5L)
//        user.setName("Zari")
//        user.setSurname("Əlili")
//        user.setEmail("zari@example.com")
//
//        when:
//        UserResponse response = userMapper.toUserResponse(user)
//
//        then:
//        response != null
//        response.id == 5L
//        response.name == "Zari"
//        response.surname == "Əlili"
//        response.email == "zari@example.com"
//    }
//}