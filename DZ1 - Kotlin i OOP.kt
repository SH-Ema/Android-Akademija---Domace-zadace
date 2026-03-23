object TransactionLogger {
    fun log(poruka: String) {
        println(poruka)
    }
}

class BankAccount(val accountNumber: String) {
    var balance = 0.0

    init {
        totalAccounts++
    }

    fun deposit(amount: Double) {
        if (amount > 0) {
            balance += amount
            TransactionLogger.log("Uplata na racun $accountNumber: $amount")
        }
    }

    fun withdraw(amount: Double) {
        if (amount > 0 && amount <= balance) {
            balance -= amount
            TransactionLogger.log("Isplata s racuna $accountNumber: $amount")
        } else {
            TransactionLogger.log("Neuspjesna isplata s racuna $accountNumber")
        }
    }

    companion object {
        var totalAccounts = 0
    }
}

fun prepareUsername(username: String): String {
    return username.trim().lowercase()
}

fun isValidUsername(username: String): Boolean {
    if (username.isBlank()) return false
    if (username.length !in 5..15) return false
    if (!username[0].isLetter()) return false
    if (!username.all { it.isLetterOrDigit() || it == '_' }) return false
    if (username.contains(" ")) return false

    return true
}

fun main() {
    // ZADATAK 1
    val ime = "Ema"
    val prezime = "Stankoski Hrgovic"
    var email: String? = null
    var age: Int? = 21

    println("Ime: $ime")
    println("Prezime: $prezime")
    println("Email length: ${email?.length}")
    println("Age: $age")

    email = "ema@gmail.com"
    println("Email length nakon promjene: ${email?.length}")

    println()

    // ZADATAK 2
    val productCode = 2
    val productPrice = 2.5
    val insertedMoney = 3.0

    val drink = when (productCode) {
        1 -> "Voda"
        2 -> "Cola"
        3 -> "Sok"
        4 -> "Kava"
        else -> "Nepoznato pice"
    }

    if (insertedMoney >= productPrice) {
        val ostatak = insertedMoney - productPrice
        println("Toci se $drink. Ostatak je $ostatak eura.")
    } else {
        val nedostaje = productPrice - insertedMoney
        println("Nedostaje jos $nedostaje eura za $drink.")
    }

    println()

    // ZADATAK 3
    val koraci = listOf(4500, 12000, 8000, 15000, 3000, 11000, 9500)

    var ukupnoKoraka = 0
    for (korak in koraci) {
        ukupnoKoraka += korak
    }
    println("Ukupno koraka u tjednu: $ukupnoKoraka")

    var i = 0
    while (i < koraci.size) {
        if (koraci[i] > 10000) {
            println("Prvi dan s vise od 10000 koraka je dan ${i + 1}")
            break
        }
        i++
    }

    println()

    // ZADATAK 4
    val unos1 = " John_doe123 "
    val unos2 = " 123marko "
    val unos3 = " Ana Mari "
    val unos4 = " iva_1 "

    val obradeno1 = prepareUsername(unos1)
    val obradeno2 = prepareUsername(unos2)
    val obradeno3 = prepareUsername(unos3)
    val obradeno4 = prepareUsername(unos4)

    println("$obradeno1 -> ${isValidUsername(obradeno1)}")
    println("$obradeno2 -> ${isValidUsername(obradeno2)}")
    println("$obradeno3 -> ${isValidUsername(obradeno3)}")
    println("$obradeno4 -> ${isValidUsername(obradeno4)}")

    println()

    // ZADATAK 5
    val racun1 = BankAccount("HR001")
    val racun2 = BankAccount("HR002")
    val racun3 = BankAccount("HR003")

    racun1.deposit(100.0)
    racun1.withdraw(30.0)

    racun2.deposit(200.0)
    racun2.withdraw(250.0)

    racun3.deposit(50.0)
    racun3.withdraw(20.0)

    println("Stanje racuna ${racun1.accountNumber}: ${racun1.balance}")
    println("Stanje racuna ${racun2.accountNumber}: ${racun2.balance}")
    println("Stanje racuna ${racun3.accountNumber}: ${racun3.balance}")
    println("Ukupno kreiranih racuna: ${BankAccount.totalAccounts}")
}