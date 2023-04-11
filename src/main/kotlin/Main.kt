fun main(args: Array<String>) {
    println("@Date")
    println("Program arguments: ${args.joinToString()}")
    val atDate = encode(args[0])
    println(atDate)
}