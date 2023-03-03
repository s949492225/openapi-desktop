fun List<Message>.modifyLast(message: String): List<Message> {
    return this.toMutableList().apply {
        val last = this.last()
        this.addAll(this.subList(0, this.size - 1))
        this += last.copy(message = message)
    }
}