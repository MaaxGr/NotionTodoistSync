import com.maaxgr.todoistnotionsync.interfaces.config.ConfigLoader
import com.maaxgr.todoistnotionsync.interfaces.config.ConfigNotion
import com.maaxgr.todoistnotionsync.interfaces.config.ConfigYaml
import com.maaxgr.todoistnotionsync.interfaces.notionrepo.NotionRepoImpl
import com.maaxgr.todoistnotionsync.interfaces.synctable.SyncTable
import com.maaxgr.todoistnotionsync.interfaces.synctable.SyncTableImpl
import kotlinx.coroutines.runBlocking
import org.jraf.klibnotion.client.Authentication
import org.jraf.klibnotion.client.ClientConfiguration
import org.jraf.klibnotion.client.NotionClient
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import java.util.*

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
                single { NotionClient.newInstance(ClientConfiguration(Authentication(get<ConfigNotion>().token))) }
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
        val syncTableImpl = SyncTableImpl()

        syncTableImpl.reloadSyncTable()

        syncTableImpl.getSyncTable().forEach { println(it) }

        //repo.addToSyncTable(NotionRepo.AddSyncTableValues("COOL", "GEHT"))

//        repo.updateTodoistId(NotionRepo.SyncTableValues(
//            pageId = "9d2b14c2-4cc7-45aa-9ddb-a7ab88a2d823",
//            notionId = "abc update",
//            todoistId = "def update"
//        ))


//        syncTableImpl.updateSyncEntry(
//            SyncTable.SyncTableEntry(
//                pageId = "9d2b14c2-4cc7-45aa-9ddb-a7ab88a2d823",
//                notionId = "abc update",
//                todoistId = "def update",
//                todoistLastUpdate = Date(System.currentTimeMillis())
//            )
//        )

        //syncTableImpl.deleteSyncEntry("85b03c51-cf16-4074-aabd-8dca29906dcc")
        //syncTableImpl.deleteSyncEntry("57f80fbd-8715-420f-b5ef-08a0d6410557")

    }


}