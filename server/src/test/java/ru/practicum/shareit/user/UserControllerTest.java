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

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserDto firstUser;
    private UserDto secondUser;


    @BeforeEach
    void startUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        firstUser = UserDto.builder()
                .id(1L)
                .name("firstUser")
                .email("first@mail.ru")
                .build();

        secondUser = UserDto.builder()
                .id(2L)
                .name("secondUser")
                .email("second@mail.ru")
                .build();
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenUsersExist_thenReturnUsers() {

        List<UserDto> users = List.of(firstUser, secondUser);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(firstUser.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(firstUser.getName())))
                .andExpect(jsonPath("$[0].email", is(firstUser.getEmail())))
                .andExpect(jsonPath("$[1].id", is(secondUser.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(secondUser.getName())))
                .andExpect(jsonPath("$[1].email", is(secondUser.getEmail())));

        verify(userService, times(1)).getAllUsers();
    }


    @SneakyThrows
    @Test
    void getAllUsers_whenUsersIsNotExist_thenReturnEmptyList() {

        List<UserDto> users = List.of();

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userService, times(1)).getAllUsers();
    }


    @SneakyThrows
    @Test
    void createUser_whenValidUser_thenReturnUser() {

        UserDto userToCreate = firstUser;

        when(userService.createUser(userToCreate)).thenReturn(userToCreate);

        String result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstUser.getName())))
                .andExpect(jsonPath("$.email", is(firstUser.getEmail())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userToCreate), result);
        verify(userService, times(1)).createUser(userToCreate);
    }

    @SneakyThrows
    @Test
    void updateUser_whenValidUser_thenReturnUser() {

        UserDto userToUpdate = firstUser;

        firstUser.setName("newName");
        firstUser.setEmail("new@mail.ru");

        when(userService.updateUserById(1L, userToUpdate)).thenReturn(userToUpdate);

        String result = mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstUser.getName())))
                .andExpect(jsonPath("$.email", is(firstUser.getEmail())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userToUpdate), result);
        verify(userService, times(1)).updateUserById(1L, userToUpdate);
    }

    @SneakyThrows
    @Test
    void getUserById_whenValidUserId_thenReturnUser() {

        Long userId = 1L;

        when(userService.getUserById(userId)).thenReturn(firstUser);

        String result = mockMvc.perform(get("/users/{usersId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstUser.getName())))
                .andExpect(jsonPath("$.email", is(firstUser.getEmail())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(firstUser), result);
        verify(userService, times(1)).getUserById(userId);
    }


    @SneakyThrows
    @Test
    void deleteUser_whenValidId_thenDelete()  {

        Long userId = 1L;

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUserById(userId);
    }
}
