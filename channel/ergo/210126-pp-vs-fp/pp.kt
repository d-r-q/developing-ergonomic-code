(def users-service (new-users-service conn))
(my-app.users/reset-password usersService user)

val users-service = new-users-service(conn)
my-app.users/reset-password users-service(user)

val usersService = newUsersService(conn)
my_app.users/resetPassword usersService(user)

val usersService = UserService(conn)
usersService.resetPassword(user)

val usersService: UsersService = UserServiceImpl(conn)
usersService.resetPassword(user)
