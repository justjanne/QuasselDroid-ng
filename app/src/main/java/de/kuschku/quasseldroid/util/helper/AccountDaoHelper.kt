package de.kuschku.quasseldroid.util.helper

import de.kuschku.quasseldroid.persistence.AccountDatabase

fun AccountDatabase.AccountDao.new(vararg entities: AccountDatabase.Account) {
  val ids = create(*entities)
  for (i in 0 until entities.size) {
    entities[i].id = ids[i]
  }
}
