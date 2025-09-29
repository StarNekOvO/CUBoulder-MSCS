package test.milk.products

import io.milk.database.DatabaseTemplate
import io.milk.products.ProductDataGateway
import io.milk.products.PurchaseInfo
import io.milk.testsupport.testDataSource
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductDataGatewayTest {
    private val dataSource = testDataSource()

    @Before
    fun before() {
        DatabaseTemplate(dataSource).apply {
            execute("delete from products")
            execute("insert into products(id, name, quantity) values (101000, 'milk', 42)")
            execute("insert into products(id, name, quantity) values (102000, 'bacon', 52)")
            execute("insert into products(id, name, quantity) values (103000, 'tuna', 62)")
        }
    }

    @Test
    fun create() {
        val gateway = ProductDataGateway(dataSource)
        val product = gateway.create("eggs", 10)
        assertTrue(product.id > 0)
        assertEquals("eggs", product.name)
        assertEquals(10, product.quantity)
    }

    @Test
    fun selectAll() {
        val gateway = ProductDataGateway(dataSource)
        val products = gateway.findAll()
        assertEquals(3, products.size)
    }

    @Test
    fun findBy() {
        val gateway = ProductDataGateway(dataSource)
        val product = gateway.findBy(101000)!!
        assertEquals("milk", product.name)
        assertEquals(42, product.quantity)
    }

    @Test
    fun update() {
        val gateway = ProductDataGateway(dataSource)
        val product = gateway.findBy(101000)!!

        product.quantity = 44
        gateway.update(product)

        val updated = gateway.findBy(101000)!!
        assertEquals("milk", updated.name)
        assertEquals(44, updated.quantity)
    }

    @Test
    fun decrementBy() {
        val gateway = ProductDataGateway(dataSource)
        gateway.decrementBy(PurchaseInfo(101000, "milk", 2))

        val updated = gateway.findBy(101000)!!
        assertEquals("milk", updated.name)
        assertEquals(40, updated.quantity)
    }

    @Test
    fun fasterDecrementBy() {
        val gateway = ProductDataGateway(dataSource)
        gateway.fasterDecrementBy(PurchaseInfo(101000, "milk", 2))

        val updated = gateway.findBy(101000)!!
        assertEquals("milk", updated.name)
        assertEquals(40, updated.quantity)
    }
}