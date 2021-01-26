val resultSet = usersTable.findUser(id)
val user = User(rs)
user.resetPassword()
user.saveTo(db)
