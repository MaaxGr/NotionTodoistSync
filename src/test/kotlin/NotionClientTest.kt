import com.maaxgr.todoistnotionsync.interfaces.config.ConfigLoader
import com.maaxgr.todoistnotionsync.interfaces.config.ConfigYaml
import com.maaxgr.todoistnotionsync.interfaces.notionrepo.NotionRepoImpl
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class NotionClientTest {

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
                single { get<ConfigYaml>().notion }
            }
            modules(mainModule)
        }
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun test(): Unit = runBlocking {
        val repo = NotionRepoImpl()
        val syncTable = repo.getSyncTable()

        syncTable.forEach { println(it) }

        //repo.addToSyncTable(NotionRepo.AddSyncTableValues("COOL", "GEHT"))

//        repo.updateTodoistId(NotionRepo.SyncTableValues(
//            pageId = "9d2b14c2-4cc7-45aa-9ddb-a7ab88a2d823",
//            notionId = "abc update",
//            todoistId = "def update"
//        ))


        repo.deleteSyncEntry("85b03c51-cf16-4074-aabd-8dca29906dcc")
        repo.deleteSyncEntry("57f80fbd-8715-420f-b5ef-08a0d6410557")

    }


}