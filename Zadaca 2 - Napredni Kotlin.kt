interface Trainable{
    fun train()
}

abstract class Person(
    val firstName: String,
    val lastName: String,
    val age: Int
){
    abstract fun info()
}

data class Stats(
    var points: Int,
    var blocks: Int,
    var aces: Int
)

open class Player(
    firstName: String,
    lastName: String,
    age: Int,
    val position: String,
    var stats: Stats
): Person (firstName, lastName, age), Trainable{
    override fun info() {println("$firstName $lastName - Points: ${stats.points}")}
    override fun train() {println("$firstName $lastName trenutno trenira")}
    open fun play(){println("$firstName $lastName igra $position")}
}

class Libero(
    firstName: String,
    lastName: String,
    age: Int,
    stats: Stats
) : Player(firstName, lastName, age, "Libero", stats) {

    override fun play() { println("$firstName prima servis i igra obranu!") }
}

class Setter(
    firstName: String,
    lastName: String,
    age: Int,
    stats: Stats
) : Player(firstName, lastName, age, "Setter", stats) {

    override fun play() { println("$firstName diže loptu!") }
}

interface Motivator{
    fun motivate()
}

class Coach (val name: String): Motivator{
    override fun motivate() {println("$name: bravo!")}
}

class Team(
    val teamName: String,
    val coach: Coach
){
    val players = mutableListOf<Player>()

    fun addPlayer(player: Player) {players.add(player)}

    fun showPlayers() {
        println("Igraci ekipe $teamName:")
        players.forEach { it.info() }
    }

    fun trainAll() {
        players.forEach { it.train() }
    }
}

fun main() {
    val coach = Coach("Mihaela")
    val team = Team("OK Linga", coach)

    val p1 = Libero("Ana", "Peić", 20, Stats(5, 2, 1))
    val p2 = Setter("Marija", "Srdarević", 21, Stats(3, 1, 4))
    val p3 = Player("Lea", "Bešlić", 22, "Middle", Stats(15, 4, 2))

    team.addPlayer(p1)
    team.addPlayer(p2)
    team.addPlayer(p3)

    coach.motivate()
    team.showPlayers()
    team.trainAll()
}