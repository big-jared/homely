package family.data

class FamilyRepository(val familyDataSource: FamilyDataSource) {
    val currentFamily: Family? get() = _currentFamily
    private var _currentFamily: Family? = null

    suspend fun initialize() {
        if (_currentFamily == null) {
            _currentFamily = familyDataSource.getFamily()
        }
    }

    suspend fun update(family: Family) {
        familyDataSource.putFamily(family)
        _currentFamily = family
    }
}
