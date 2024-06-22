package net.ddellspe.escapegenai.repository

import java.util.*
import net.ddellspe.escapegenai.model.Password
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Repository

@Repository interface PasswordRepository : ListCrudRepository<Password, UUID> {}
