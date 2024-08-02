package family.data

interface FamilyDataSource {
    suspend fun putFamily(family: Family)
    suspend fun getFamily(): Family?
}

class DemoFamilyDataSource(private var existingFamily: Family? = null) : FamilyDataSource {
    override suspend fun putFamily(family: Family) {
        existingFamily = family
    }

    override suspend fun getFamily(): Family? = existingFamily
}
