package test.milk.rabbitmq

import com.rabbitmq.client.CancelCallback

class TestCancelHandler : CancelCallback {
    override fun handle(consumerTag: String?) {
    }
}