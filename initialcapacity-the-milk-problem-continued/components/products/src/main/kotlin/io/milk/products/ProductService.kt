package io.milk.products

class ProductService(private val dataGateway: ProductDataGateway) {
    fun findAll(): List<ProductInfo> {
        return dataGateway.findAll().map { ProductInfo(it.id, it.name, it.quantity) }
    }

    fun findBy(id: Long): ProductInfo {
        val record = dataGateway.findBy(id)!!
        return ProductInfo(record.id, record.name, record.quantity)
    }

    fun update(purchase: PurchaseInfo): ProductInfo {
        val record = dataGateway.findBy(purchase.id)!!
        record.quantity -= purchase.amount
        dataGateway.update(record)
        return findBy(record.id)
    }

    fun decrementBy(purchase: PurchaseInfo) {
        // TODO - DIRTY READS - Implement the decrementBy function.
    }
}