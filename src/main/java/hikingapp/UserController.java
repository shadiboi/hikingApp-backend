package hikingapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    // as another route listed after class variables

    @PostMapping("/login")
    public User login(@RequestBody User login, HttpSession session) throws Exception{
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        User user = userRepository.findByUsername(login.getUsername());
        if(user ==  null){
            throw new Exception("Invalid Credentials");
        }
        boolean valid = bCryptPasswordEncoder.matches(login.getPassword(), user.getPassword());
        if(valid){
            session.setAttribute("username", user.getUsername());
            System.out.println((String)session.getAttribute("username"));
            return user;
        }else{
            throw new Exception("Invalid Credentials");
        }
    }


    @PostMapping("/users")
    public User createUser(@RequestBody User user,  HttpSession session){
        User createdUser = userService.saveUser(user);
        session.setAttribute("username", user.getUsername());
        return createdUser;
    }

    @GetMapping("/current")
    public User currentUser(HttpSession session){
        User currentUser = userRepository.findByUsername((String)session.getAttribute("username"));
        return currentUser;
    }

    @GetMapping("/users")
    public Iterable<User> getUsers(){
        return userRepository.findAll();
    }



    @PostMapping("/logout")
    public User logout(HttpSession session) {
        if (session.getAttribute("username") != null) {
            session.invalidate();
        }
        return null;
    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable("id") Long id){
        userRepository.deleteById(id);
        return "deleted user " + id;
    }

    @PutMapping("/users/{id}")
    public User updateUser(@RequestBody User formData, @PathVariable("id") Long id) throws Exception{
        Optional<User> response = userRepository.findById(id);
        if(response.isPresent()){
            User user = response.get();
            user.setLocation(formData.getLocation());
            return userRepository.save(user);
        }
        throw new Exception("no such user");
    }

}