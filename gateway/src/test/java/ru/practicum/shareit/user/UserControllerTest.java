package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserDto;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserClient userClient;
    @InjectMocks
    private UserController userController;
    private MockMvc mockMvc;
    private UserDto firstUser;
    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void startUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        firstUser = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@mail.ru")
                .build();
    }

    @SneakyThrows
    @Test
    void createUser_whenEmptyUserName_thenExceptionThrown() {

        firstUser.setName("");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(firstUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verifyNoInteractions(userClient);
    }

    @SneakyThrows
    @Test
    void createUser_whenLargestUserName_thenExceptionThrown() {

        firstUser.setName("Божиею поспешествующею милостию, Мы, Александр Третий, Император и " +
                "Самодержец Всероссийский, Московский, Киевский, Владимирский, Новгородский, Царь Казанский, " +
                "Царь Астраханский, Царь Польский, Царь Сибирский, Царь Херсониса Таврического, Царь Грузинский; " +
                "Государь Псковский и Великий Князь Смоленский, Литовский, Волынский, Подольский и Финляндский; " +
                "Князь Эстляндский, Лифляндский, Курляндский и Семигальский, Самогитский, Белостокский, Корельский, " +
                "Тверский, Югорский, Пермский, Вятский, Болгарский и иных; Государь и " +
                "Великий Князь Новагорода Низовския земли, Черниговский, Рязанский, Полотский, Ростовский, " +
                "Ярославский, Белоозерский, Удорский, Обдорский, Кондийский, Витебский, " +
                "Мстиславский и всея Северныя страны Повелитель, и Государь Иверския, " +
                "Карталинския и Кабардинския земли и Армянския области, " +
                "Черкасских и Горских Князей и иных наследный Государь и Обладатель, " +
                "Государь Туркестанский, Наследник Норвежский, Герцог Шлезвиг-Голстинский, " +
                "Стормарнский, Дитмарсенский и Ольденбургский и прочая, и прочая, и прочая.");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(firstUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verifyNoInteractions(userClient);
    }

    @SneakyThrows
    @Test
    void createUser_whenEmptyEmail_thenExceptionThrown() {
        firstUser.setEmail("");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(firstUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verifyNoInteractions(userClient);
    }

    @SneakyThrows
    @Test
    void createUser_whenLargestEmail_thenExceptionThrown() {

        firstUser.setEmail("such-big-much-wow-email-large-awesome-tremendous-fabulous-doggo-without-registration" +
                "-right-now-cake-is-not-exist-cake-it-is-a-lie-half-life-3" +
                "-gabe-stop-slacking-you-greedy-ogre-shrek-is-love-shrek-is-life-@bigemail.com");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(firstUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verifyNoInteractions(userClient);
    }

    @SneakyThrows
    @Test
    void createUser_whenInvalidEmailPattern_thenExceptionThrown() {

        firstUser.setEmail("user.mail.ru");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(firstUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verifyNoInteractions(userClient);
    }

    @SneakyThrows
    @Test
    void updateUser_whenLargestUserName_thenExceptionThrown() {

        firstUser.setName("Божиею поспешествующею милостию, Мы, Александр Третий, Император и " +
                "Самодержец Всероссийский, Московский, Киевский, Владимирский, Новгородский, Царь Казанский, " +
                "Царь Астраханский, Царь Польский, Царь Сибирский, Царь Херсониса Таврического, Царь Грузинский; " +
                "Государь Псковский и Великий Князь Смоленский, Литовский, Волынский, Подольский и Финляндский; " +
                "Князь Эстляндский, Лифляндский, Курляндский и Семигальский, Самогитский, Белостокский, Корельский, " +
                "Тверский, Югорский, Пермский, Вятский, Болгарский и иных; Государь и " +
                "Великий Князь Новагорода Низовския земли, Черниговский, Рязанский, Полотский, Ростовский, " +
                "Ярославский, Белоозерский, Удорский, Обдорский, Кондийский, Витебский, " +
                "Мстиславский и всея Северныя страны Повелитель, и Государь Иверския, " +
                "Карталинския и Кабардинския земли и Армянския области, " +
                "Черкасских и Горских Князей и иных наследный Государь и Обладатель, " +
                "Государь Туркестанский, Наследник Норвежский, Герцог Шлезвиг-Голстинский, " +
                "Стормарнский, Дитмарсенский и Ольденбургский и прочая, и прочая, и прочая.");

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .content(objectMapper.writeValueAsString(firstUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verifyNoInteractions(userClient);
    }

    @SneakyThrows
    @Test
    void updateUser_whenLargestEmail_thenExceptionThrown() {

        firstUser.setEmail("such-big-much-wow-email-large-awesome-tremendous-fabulous-doggo-without-registration" +
                "-right-now-cake-is-not-exist-cake-it-is-a-lie-half-life-3" +
                "-gabe-stop-slacking-you-greedy-ogre-shrek-is-love-shrek-is-life-@bigemail.com");

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .content(objectMapper.writeValueAsString(firstUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verifyNoInteractions(userClient);
    }

    @SneakyThrows
    @Test
    void updateUser_whenInvalidEmailPattern_thenExceptionThrown() {

        firstUser.setEmail("user.mail.ru");

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .content(objectMapper.writeValueAsString(firstUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verifyNoInteractions(userClient);
    }
}