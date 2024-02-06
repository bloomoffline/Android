
package com.lab.tb.distributed.mapper.base

interface IDbEntityMapper<B, E> : IMapper {

    fun fromDbToEntity(db: B?): E?

    fun fromEntityToDb(entity: E?): B?

    fun fromDbToEntityList(dbList: List<B>?): List<E>? {
        throw NotImplementedError()
    }
}
