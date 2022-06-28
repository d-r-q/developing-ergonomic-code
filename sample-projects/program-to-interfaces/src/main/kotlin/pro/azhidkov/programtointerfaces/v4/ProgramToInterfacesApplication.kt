package pro.azhidkov.programtointerfaces.v4

import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


class User(
    var id: Long,
    var login: String,
    var password: String
)

interface UsersRepo {
    fun findByIdOrNull(id: Long): User?

    fun save(user: User)
}

@Repository
class InMemUsersRepo : UsersRepo {

    private val data = HashMap<Long, User>()

    override fun findByIdOrNull(id: Long): User? = data[id]

    override fun save(user: User) {
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
