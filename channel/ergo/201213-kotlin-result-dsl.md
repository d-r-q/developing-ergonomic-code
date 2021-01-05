**Kotlin Result DSL**

И кстати в догонку про обработку ошибок.

Я в своё время прям сильно впечатлился Rust-овским try!. (Который уже оказывается стал ?-оператором - https://doc.rust-lang.org/edition-guide/rust-2018/error-handling-and-panics/the-question-mark-operator-for-easier-error-handling.html)

И недавно запилил драфт похожей для штуки для Котлина:
```kotlin
fun testReturn(): WorkflowResult<Int, Throwable> {
    val int = doIo()
        .onError { return it }
    return Result(int + 1)
}
```

Это аналог вот такому коду:
```kotlin
fun testLong(): WorkflowResult<Int, Throwable> {
    val res = doIo()
    if (res is Error) {
        return res
    }
    val int = (res as Success).result

    return Result(int + 1)
}
```

Совсем однострочник сделать не получается, т.к. в Котлине нет полноценных макросов, а для того чтобы это приём абстрагировать в функцию, в ней надо сделать ретарн из произвольной вызывающей функции с неизвестным типом возврата.

Но спрашивается зачем всё это?

В Котлине возврат того или иного Result - это единственный способ заставить вызывающий код обработать ошибку.
Но если это делать наивно, то получается код как из втрого примера.
Вообще Result-ы как правило являются монадами и с ними можно работать так:
```kotlin
    val res = doIo()
    return res.map { it + 1}
```
И так получается даже короче. Но (в Котлине по крайней мере) монады не масштабируются.
Например, если надо выполнить ИО-операцию на базе результата первых двух (и только в случае их успеха), то будет вот такой адок:
```kotlin
fun testCompositeM(): Optional<Int> {
    val first = mIo()
    val second = first.flatMap { mIo2(it) }
    return first.flatMap { firstVal ->
        second.flatMap { secondVal ->
            mIo2(firstVal + secondVal)
        }
    }
}
```

С моим же DSL-ем, всё будет прилично:
```kotlin
fun testComposite(): WorkflowResult<Int, Throwable> {
    val first = doIo().onError { return it }
    val second = doIo2(first).onError { return it }
    return doIo2(first + second)
}
```

Полный код: https://gist.github.com/d-r-q/45da53ff4ecadfe1a30b70be4428f29e.
