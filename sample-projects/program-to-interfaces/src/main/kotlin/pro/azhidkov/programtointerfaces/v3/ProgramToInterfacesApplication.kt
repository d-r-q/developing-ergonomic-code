package pro.azhidkov.programtointerfaces.v3

import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


class User(
    var id: Long,
    var login: String,
    var password: String
)

@Repository
class UsersRepo {

    private val data = HashMap<Long, User>()

    fun findByIdOrNull(id: Long): User? = data[id]

    fun save(user: User) {
        data[user.id] = user
    }

}

@Service
class UsersService(
    private val usersRepo: UsersRepo
) {

    @Transactional
    suspend fun updatePassword(id: Long, newPass: String) {
        val user = usersRepo.findByIdOrNull(id)!!
        user.password = newPass
        usersRepo.save(user)
    }

}
