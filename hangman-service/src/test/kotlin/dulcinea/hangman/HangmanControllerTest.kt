package dulcinea.hangman

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [HangmanController::class], webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@WebAppConfiguration
@AutoConfigureMockMvc
@EnableWebMvc
class HangmanControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var hangmanService: HangmanService

    @Test
    fun `can create a new game`() {
        mockMvc.perform(MockMvcRequestBuilders.post("/game"))
                .andExpect(MockMvcResultMatchers.status().isOk())

        Mockito.verify(hangmanService, Mockito.times(1)).newGame()
    }

    @Test
    fun `can get the current status`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/game"))
                .andExpect(MockMvcResultMatchers.status().isOk())

        Mockito.verify(hangmanService, Mockito.times(1)).getStatus()
    }

    @Test
    fun `can guess a letter`() {
        mockMvc.perform(MockMvcRequestBuilders.put("/game/A"))
                .andExpect(MockMvcResultMatchers.status().isOk())

        Mockito.verify(hangmanService, Mockito.times(1)).makeGuess("A")
    }
}