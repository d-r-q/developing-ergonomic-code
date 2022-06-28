package pro.azhidkov.programtointerfaces.v1

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.Entity
import javax.persistence.Id


@Entity
class User(
    @Id var id: Long,
    var login: String,
    var password: String
)

@Repository
interface UsersRepo : CrudRepository<User, Long>

@Service
class UsersService( // тут по идеи тоже должен быть интерфейс, но сократим его ради экономии экрана
    private val usersRepo: UsersRepo
) {

    @Transactional
    fun updatePassword(id: Long, newPass: String) {
        val user = usersRepo.findByIdOrNull(id)!!
        user.password = newPass
    }

}