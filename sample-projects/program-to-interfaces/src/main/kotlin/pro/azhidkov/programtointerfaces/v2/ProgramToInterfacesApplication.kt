package pro.azhidkov.programtointerfaces.v2

import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Table
class User(
    var id: Long,
    var login: String,
    var password: String
)

@Repository
interface UsersRepo : CrudRepository<User, Long>

@Service
class UsersService(
    private val usersRepo: UsersRepo
) {

    @Transactional
    fun updatePassword(id: Long, newPass: String) {
        val user = usersRepo.findByIdOrNull(id)!!
        user.password = newPass
        usersRepo.save(user)
    }

}