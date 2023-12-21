# Data-Service

## Założenia i opis

Serwis danowy zapewnienia abstrakcję modeli danych, które użytkownik może wprowadzać do systemu. Modele mają być
skrojone pod jego aktualne potrzeby gwarantując niemal optymalne działanie. Serwis danowy przechowuje jedynie dane
biznesowe z pominięciem danych funkcjonalnych (jak dane formularzy, procesów itp)

Przykładowym modelem bazowym może być Klient (Customer):

```kotlin
open class Customer {
    open var id: Long?
    open var name: String?
    open var alias: String?
    open var taxNumber: String?
    open var projects: MutableList<Project>?
}
```

Model bazowy powinien spełniać następujące zasady:

- zawierać minimalną ilość pól
- zawierać tylko te pola, które są niezbędne do zapewnienia potrzebnej funkcjonalności
- wszelkie rozszczerzenia modelu powinny być realizowane przez dodanie relacji

Serwis danowy udostępnia metadane swoich zaimplementowanych modeli w następującej postaci:

```json
[
  {
    "name": "id",
    "type": "Long",
    "constraints": [
      "Id"
    ],
    "multiplicity": "SINGULAR"
  },
  {
    "name": "name",
    "type": "String",
    "constraints": [],
    "multiplicity": "SINGULAR"
  },
  {
    "name": "alias",
    "type": "String",
    "constraints": [],
    "multiplicity": "SINGULAR"
  },
  {
    "name": "taxNumber",
    "type": "String",
    "constraints": [],
    "multiplicity": "SINGULAR"
  },
  {
    "name": "projects",
    "type": "List<prodactivvity.Project>",
    "constraints": [],
    "multiplicity": "PLURAL"
  }
]
```

Metadane modelu opisują wszystkie udostępnione do odczytu/zapisu pola wybranego modelu (w tym przykładzie jest to
Customer). Każde pole zawiera nazwę, typ, listę ograniczeń oraz w przypadku relacji jej krotność.

Ograniczenia nałożone na dane pole wynikają bezpośrednio z potrzeby biznesowej, które dany model ma realizować.

Wszelkie zmiany wprowadzane do modelu powinny być starannie przemyślane i oparte na potrzebach biznesowych, aby nie
wprowadzać niepotrzebnych skomplikowań i utrudnień w korzystaniu z serwisu.

## Rozwój

Rozwój serwisu w głównej mierze opiera się o definiowanie nowych modeli lub rozszerzanie istniejących. Przed wdrażaniem
jakichkolwiek zmian, należy w pierwszej kolejności sporządzić diagram relacji i zweryfikować jego poprawność. Trzeba
pamiętać aby zmiany, które będą wprowadzane nie wpływały na zmianę działania istniejących rozwiązań. Tworząc nowe modele
należy się starać aby spełniały 3NF.

### Dodanie modelu - technicznie

Należy model umieścić w odpowiadającym mu module funkcjonalności, czyli w odpowiednim pakiecie, np.: organization,
invoices itp. Pozwala to zachować porządek i grupuje modele według funkcji, które realizują. Model należy dodać jako
osobną klasę Kotlinową z zachowaniem następujących zasad:

- Zawsze oznaczamy model adnotacją `@Entity`
- Klasa musi być “otwarta” `open class Task`
- Dla wartości ID zawsze definiujemy generator sekwencji np.:

```kotlin
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Task_GEN")
@SequenceGenerator(name = "Task_GEN", sequenceName = "Task_SEQ")
open var id: Long? = null
```

- Każde pole w modelu musi być `open` oraz `nullable` z domyślną wartością `null`
  (jest to wymóg nałożony przez Framework, tylko w ten sposób współpracuje z Kotlinem i jego podejściem `nullsafe`
- Do każego pola można dodać opcjonalne, niestandardowe adnotacje, nakładajace dodatkowe ograniczenia na model:
  - `@NotInsertable` - wybrane pole nie może dynamicznie tworzyć nowych obiektów
  - `@Required` - wybrane pole jest obowiązkowe
- Bardzo ważny jest element `companion object` gdzie zdefiniowany jest `CLASS_TYPE` w notacji `moduł.Typ` potrzebny do
  prawidłowego działania modelu w serwisie.

Przykład nowego modelu:

```kotlin
@Entity
open class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Task_GEN")
    @SequenceGenerator(name = "Task_GEN", sequenceName = "Task_SEQ")
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @field:Required
    open var name: String? = null

    @Lob
    open var description: String? = null

    open var orderedOn: LocalDateTime? = null

    open var deadline: LocalDateTime? = null

    @ManyToOne
    @JoinColumn(name = "orderer_id")
    @field:Required
    @field:NotInsertable
    open var orderer: AppUser? = null

    @ManyToOne
    @JoinColumn(name = "performer_id")
    @field:Required
    @field:NotInsertable
    open var performer: AppUser? = null

  @ManyToOne
  @JoinColumn(name = "customer_id")
  @field:NotInsertable
  open var customer: Customer? = null

  @ManyToOne
  @JoinColumn(name = "project_id")
  open var project: Project? = null

  companion object {
    const val CLASS_TYPE = "agile.Task"
  }
}
```

Pole `orderer` jest oznaczone niestandardową adnotacją `@NotInsertable` co informuje, że np.: z wykorzystaniem
formularza
w tym polu nie można dynamicznie utworzyć nowego użytkownika i można wybrać jedynie spośród istniejących użytkowników.

Kolejnym ważnym elementem układanki jest stworzenie serwisu dla tego modelu, serwis musi pozwalać na odczyt danych i ich
zapis.
Realizuje się to za pomocą domyślnego interfejsu np.:

```kotlin
@Repository
interface TaskRepository : JpaRepository<Task, Long> {}
```

Sam interfejs nie wymaga póki co specjalnych metod wewnątrz i może pozostać pusty. Drugim krokiem jest stworzenie
serwisu:

```kotlin
@Service(Task.CLASS_TYPE)
class TaskPersistenceService(
  private val repository: TaskRepository,
  metadataExtractor: MetadataExtractor,
) : AbstractPersistenceService<Task>(
  metadataExtractor,
  Task::class.java,
  Task.CLASS_TYPE,
) {
  override fun save(x: Any): Any {
    return repository.save(x as Task)
  }

  override fun getRepository(): JpaRepository<Task, Long> {
    return repository
  }

  override fun addJoinsWithPredicates(proQLQuery: ProQLQuery): MutableList<Predicate> {
    val joinPredicates = mutableListOf<Predicate>()

    proQLQuery.subQueries?.forEach {
      when (it.parentProperty) {
        "descriptionExtension" -> {
          joinPredicates.addAll(
            join(it, Description.CLASS_TYPE)
          )
        }

        "orderer", "performer" -> {
          joinPredicates.addAll(
            join(it, SysUser.CLASS_TYPE)
          )
        }

        "project" -> {
          joinPredicates.addAll(
            join(it, Project.CLASS_TYPE)
          )
        }

        "capacity" -> {
          joinPredicates.addAll(
            join(it, Capacity.CLASS_TYPE)
          )
        }

        "evaluation" -> {
          joinPredicates.addAll(
            join(it, Evaluation.CLASS_TYPE)
          )
        }

        else -> throw UnknownRelationException()
      }
    }

    return joinPredicates
  }
}
```

Najważniejszym punktem jest rozszerzenie klasy `AbstractPersistenceService<Type>` podając za `Type` typ biznesowy, który
rozpatrujemy. Kontruktor tej klasy przyjmuje 3 argument:

- MetadataExtractor, jest to komponent serwisu pozwalający wyciągać informacje o konstrukcji typu `Type`,
- Class, jest to argument przyjmujący `Javowy` typ classy typu `Type` np.: `Task::class.java`,
- ClassType, jest to argument typu `String` gdzie podany jest typ w notacji `moduł.Typ`, każdy typ biznesowy musi mieć
  zdefiniowaną tą stałą

Sama klasa serwisu dla konkretnego typu biznesowego np.: `agile.Task` musi być otagowana adnotacją `@Service` z podaniem
w argumencie `CLASS_TYPE` dla tego typu. Jest to potrzebne do zapisu i odczytu danych za pomocą komponentu `ProQL`.
W konstruktorze tego serwisu należy zdefiniować atrybut dla `Repository` do tego typu aby Spring mógł wstrzyknąć tą
zależność,
dodatkowo należy przekazać komponent `MetadataExtractor` potrzebny dla klasy rodzicielskiej.

Rozszerzenie klasy `AbstractPersistenceService` wymusi implementację następujących metod:

```kotlin
fun save(x: Any): Any
fun getRepository(): JpaRepository<T, Long>
fun addJoinsWithPredicates(proQLQuery: ProQLQuery): MutableList<Predicate>
```

- funkcja `save` odpowiedzialna jest za zapis przekazanej encji, przekazywana jest jako `Any`, dlatego w niej należy `x`
  zrzutować na model `Task`:
  ```kotlin
  override fun save(x: Any): Any {
      return repository.save(x as Task)
  }
  ```
- funkcja `getRepository` odpowiedzialna jest za przekazanie repozytorium do klasy rodzica i implementacja tej metody
  zawsze jest stała:
  ```kotlin
  override fun getRepository(): JpaRepository<Task, Long> {
      return repository
  }
  ```
- funkcja `addJoinsWithPredicates` jest implementowana wyłącznie wtedy kiedy typ np.: `agile.Task` ma w swojej definicji
  określone relacje z innmi modelami. W przykładzie powyżej ta metoda jest zdefiniowana, bo typ `agile.Task` zawiera w
  swojej
  definicji odwołania do innych typów np.: pole `ordered` i `performer` odwołują się do typu `core.SysUser` i w
  implementacji
  tej metody są te pola ujęte:
  ```kotlin
  "orderer", "performer" -> {
      joinPredicates.addAll(
          join(it, SysUser.CLASS_TYPE)
      )
  }
  ```
  I ten kawałek kodu nie robi nic więcej jak dodanie poleceń `JOIN` do `SQL query` kiedy ta funkcja otrzyma `ProQLQuery`
  z określonym warunkiem dla pola `orderer` lub `performer`

## Abstrakcja modeli

Ta koncepcja zakłada, że modele biznesowe opisujemy od razu na modelu bazodanowym. Czyli tak jak jest opisane wyżej.
Model na którym pracują aplikacje i użytkownicy są bezpośrednim odwzorowaniem bazy danych. Czyli są to klasy oznaczone
jako `@Entity`. Takie rozwiązanie ma następujące zalety:

- prostsza implementacja, czyli prostsze dodawanie nowych modeli - ograniczamy się jedynie do dodania modelu,
  zdefiniowania repozytorium oraz implementacji ProQL
- prostsze pobieranie i zapis danych - realizowane w głównej mierze poprzez Hibernate’a i automatycznie generowane
  zaptania SQL

ale ma też wady:

- z biegiem czasu takie rozwiązanie może się okazać nieoptymalne, z powodu automatycznego generowania zapytań SQL

![Untitled](docs/Untitled.png)

## ProQL

Jest głównym mechanizmem odpowiedzialnym za działanie tego serwisu. `ProQL` odpowiada za ujednolicony interfejs
pobierania i zapisu danych. Dzieli się na `ProQLQuery` -pobieranie danych oraz `ProQLCommand`-zapisywanie danych.

- `ProQLQuery` - obiekt transferu danych odpowiadających za pobranie danych z bazy, składa się z 4 elementów 
(2 opcjonalnych odpowiadających za paginację odpowiedzi)
  - `type` - nazwa typu w notacji `moduł.Typ` np.: `agile.Task` - odpowiada za określenie jakiego typu danych szukamy
  - `properties` - atrybuty tego typu - odpowiada za możliwość określenia jakie wartości mają mieć poszukiwane obiekty
    wybranego typu
  - `page` - **pole opcjonalne** pozwalające określić stronę do wyświetlenia
  - `size` - **pole opcjonalne** pozwalające określić rozmiar strony
  - `subQueries` - jest to w zasadzie zagnieżdżony `ProQLQuery` pozwalający określić jakie wartości maja mieć powiązane
    obiekty, od samego `ProQLQuery` różni się jedynie tym, że posiada dodatkowy atrybut `parentProperty`, który określa
    który atrybut klasy rodzica bierzemy pod uwagę
- `ProQLCommand` - obiekt transferu danych odpowiadających za utworzenie nowych rekordów, składa się z 4 elementów
  - `type` - nazwa typu w notacji `moduł.Typ` np.: `agile.Task` - odpowiada za określenie jaki typ danych będzie
    tworzony
  - `parentProperty` - wykorzystywany jest tylko w zagnieżdżonych `ProQLCommands` \[`commands`\] i pozwala na powiązanie
    nowo tworzonego obiektu z istniejącym rekordem innego typu, lub jednoczesne utworzenie nowego typu powiązanego
  - `properties` - atrybuty tworzonego obiektu
  - `commands` - wykorzystuje się to w sytuacjach kiedy tworzony obiekt jest w relacji z innym obiektem,
    np.: `agile.Task`
    jest powiązany z `core.SysUser` poprzez pole `orderer` oraz `performer`, w tej sytuacji można poprzez `commands`
    przekazać tą informację

### Przykłady `ProQL`

- `ProQLCommand`:
  <details>
  <summary>Zobacz przykład</summary>

  Poniższy command pozwala zapisać nowy obiekty typu `agile.Task` przy jednoczesnym utworzeniu nowego obiektu typu
  - `agile.Capacity`

  oraz wykorzystaniu istniejących już obiektów typu:
  - `agile.Project` - o id `8`
  - `core.SysUser` - o id `26` i `14`

  ```json
  {
    "type": "agile.Task",
    "properties": {
      "name": "Naprawa masowego dodawania nagród do magazynu ",
      "dateOfCreation": "2023-01-30",
      "dueDate": "2023-01-30",
      "endDate": "2023-06-16",
      "sprint": "B2-2023-07-02",
      "status": "To fix",
      "type": "Backend",
      "urgency": "1. Krytyczne (only critical)",
      "priority": 94
    },
    "commands": [
      {
        "type": "agile.Capacity",
        "parentProperty": "capacity",
        "properties": {
          "budget": 8,
          "estimation": 8,
          "timeLeft": 7.84,
          "timeConsumed": 13.16
        }
      },
      {
        "type": "agile.Project",
        "parentProperty": "project",
        "properties": {
          "id": 8
        }
      },
      {
        "type": "core.SysUser",
        "parentProperty": "orderer",
        "properties": {
          "id": 26
        }
      },
      {
        "type": "core.SysUser",
        "parentProperty": "performer",
        "properties": {
          "id": 14
        }
      }
    ]
  }
  ```
  </details>

- `ProQLQuery`
  <details>
  <summary>Zobacz przykład</summary>

  Poniższe query pozwala pobrać wszystkie obiekty typu `agile.Task` które mają atrybut `type` = `Backend`

  ```json
  {
    "type": "agile.Task",
    "properties": {
      "type": "Backend"
    }
  }
  ```

  Poniższe query pozwala pobrać wszystkie obiekty typu `agile.Task` które mają atrybut `type` = `Backend` i są 
  podzielone na strony wielkości 10 rekordów i wyświetlamy pierwszą stronę
  Odpowiedź będzie opakowana w informacjeo ilości elementów na wszystkich stronach, ilości stron i obecnej stronie na 
  której jesteśmy
  
    ```json
    {
      "type": "agile.Task",
      "properties": {
        "type": "Backend"
      },
    "size": 10,
    "page": 0
    }
    ```
  ProQLQuery daje również możliwość filtrowania zwracanych danych poprzez odpowiednie zbudowanie `properties`.
  Dla pól z wartościami tekstowymi:
  1. jeśli chcemy odnaleźć w danej kolumnie jedną z wielu wprowadzonych wartości
  ```json
    {
      "type": "agile.Task",
      "properties": {
        "name": ["Bp2Card ", "GET na collapse"]
      }   
    }
    ```
  2. filtrowanie jednej wartości w wielu kolumnach
  ```json
    {
      "type": "agile.Task",
      "properties": {
        "name": "Bp2Card ",
        "type": "Bp2Card "
      }   
    }
    ```
  3. Wyświetlenie rekordów zawierających dokładnie to co wpisaliśmy

  ```json
    {
      "type": "agile.Task",
      "properties": {
        "name": {"equal": "GET na collapse"}
      }   
    }
    ```
  4. Wyświetlenie rokordów nie zawierających wpisaną wartość
  ```json
    {
      "type": "agile.Task",
      "properties": {
        "name": {"notEqual": "GET na collapse"}
      }   
    }
    ```
  Dla pól z wartościami Long lub Data:
  1. Od do (działa tak samo dla Long i LocalDate)
  ```json
    {
      "type": "agile.Task",
      "properties": {
        "id": {"from": "100", "to": "200"}
      }   
    }
    ```
  2. Więcej / Mniej (działa tak samo dla Long i LocalDate)
  ```json
    {
      "type": "agile.Task",
      "properties": {
        "id": {"from": "100"}
      }   
    }
    ```
  ```json
    {
      "type": "agile.Task",
      "properties": {
        "id": {"to": "200"}
      }   
    }
    ```

  </details>
---

## Komunikacja z innymi mikroserwisami

Serwis danowy komunikowac może się jedynie z serwisem Formularzy, który odpowiada za zapewnienie bezpiecznych
interfejsów dodawania i edycji danych. Na daną chwilę serwis nie powinien być udostępniony nigdzie indziej.

### Komunikacja z serwisem Formularzy

Aktualnie komunikacja opiera się o RestAPI co nie jest najlepszym rozwiązaniem, ale na ten moment jest wystarczające.

### Cacheowanie metadanych

Najistotniejsze dane które serwis danowy udostępnia serwisowi Formularzy to metadane modeli. Metadane modeli mają to do
siebie, że podczas działania aplikacji pozostają niezmienne. Można zatem pomysleć o współdzielonym cache’u tych danych,
tak aby nie męczyć zbędnie aplikacji i zapewnić natychmiastowy dostęp do tych danych. Moja propozycja to postawić np.:
Redisa i udostępnić go obu serwisom

![Untitled](docs/Untitled%202.png)

## Lista zadań do realizacji

1. Przegadać koncepcje opisane wyżej z biznesem - im szybciej tym lepiej. do PoC’a możemy zostać z aktualnie
   zastosowanym podejściem nie będzie to miało większego wpływu na wydajność póki co.
2. Opisać zasady działania `ProQLQuery` i `ProQLCommand`
3. Zadbać o observability serwisu i bazy danych
4. Zaimplementować cachowanie metadanych modeli