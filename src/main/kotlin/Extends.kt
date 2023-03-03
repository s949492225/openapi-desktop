fun List<Message>.modifyLast(message: String): List<Message> {
    val list = this
    return mutableListOf<Message>().apply {
        val last = list.last()
        this.addAll(list.subList(0, list.size - 1))
        this += last.copy(message = message)
    }
}