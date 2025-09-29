package test.milk.products

import io.milk.database.DatabaseTemplate
import io.milk.products.ProductDataGateway
import io.milk.products.ProductService
import io.milk.products.PurchaseInfo
import io.milk.testsupport.testDataSource
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ProductServiceTest {
    private val dataSource = testDataSource()

    @Before
    fun before() {
        DatabaseTemplate(dataSource).apply {
            execute("delete from products")
            execute("insert into products(id, name, quantity) values (101000, 'milk', 42)")
            execute("insert into products(id, name, quantity) values (102000, 'kombucha', 15)")
        }
    }

    @Test
    fun findAll() {
        val service = ProductService(ProductDataGateway(dataSource))
        val products = service.findAll()
        assertEquals(2, products.size)
    }

    @Test
    fun findBy() {
        val service = ProductService(ProductDataGateway(dataSource))
        val product = service.findBy(101000)
        assertEquals("milk", product.name)
        assertEquals(42, product.quantity)
    }

    @Test
    fun update() {
        val service = ProductService(ProductDataGateway(dataSource))
        service.update(PurchaseInfo(101000, "milk", 2))

        val product = service.findBy(101000)
        assertEquals("milk", product.name)
        assertEquals(40, product.quantity)
    }

    @Test
    fun decrementBy() {
        val service = ProductService(ProductDataGateway(dataSource))
        service.decrementBy(PurchaseInfo(101000, "milk", 2))

        val product = service.findBy(101000)
        assertEquals("milk", product.name)
        assertEquals(40, product.quantity)
    }
}