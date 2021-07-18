import com.maaxgr.todoistnotionsync.interfaces.config.ConfigLoader
import com.maaxgr.todoistnotionsync.interfaces.config.ConfigNotion
import com.maaxgr.todoistnotionsync.interfaces.config.ConfigYaml
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.TodoistRepo
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.TodoistRepoImpl
import com.maaxgr.todoistnotionsync.todoistmanager.TodoistManager
import org.jraf.klibnotion.client.Authentication
import org.jraf.klibnotion.client.ClientConfiguration
import org.jraf.klibnotion.client.NotionClient
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class TodoistTest {

    @Before
    fun before() {
        startKoin {
            val mainModule = module {
                single {
                    ConfigLoader().run {
                        customDir = "src/test/resources"
                        loadConfig()
                    }
                }
                single { get<ConfigYaml>().todoist }
                single<TodoistRepo> { TodoistRepoImpl() }
            }
            modules(mainModule)
        }
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun test() {
        val todoistManager = TodoistManager()

        val updates = todoistManager.getUpdatesToProcess(12568587997)
        println("Size: ${updates.size}")
    }

}