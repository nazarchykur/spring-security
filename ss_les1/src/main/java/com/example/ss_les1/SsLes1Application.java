package com.example.ss_les1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SsLes1Application {

	public static void main(String[] args) {
		SpringApplication.run(SsLes1Application.class, args);
	}

}
/*

https://www.marcobehler.com/guides/spring-security


вступ

	Рано чи пізно кожному потрібно додати захист до свого проекту, і в екосистемі Spring ви робите це за допомогою бібліотеки 
	Spring Security .
	
	Тож ви йдете, додаєте Spring Security до свого проекту Spring Boot (або просто Spring ) і раптом...
		…у вас є автоматично згенеровані сторінки входу.
		…ви більше не можете виконувати запити POST/GET/....
		…уся ваша програма заблокована та пропонує вам ввести ім’я користувача та пароль.
	
	
	Що таке Spring Security і як це працює?
		Коротка відповідь :
		
		За своєю суттю Spring Security — це лише купа сервлет-фільтрів, які допомагають вам додати автентифікацію та 
		авторизацію до вашої веб-програми.
		
		Він також добре інтегрується з такими фреймворками, як Spring Web MVC (або Spring Boot ), а також із такими стандартами, 
		як OAuth2 або SAML. Він автоматично створює сторінки входу/виходу та захищає від поширених запитів, таких як CSRF.


	Перш ніж стати Spring Security Guru, вам потрібно зрозуміти три важливі поняття:
		Аутентифікація
		Авторизація
		Фільтри сервлетів
		
		
	1. Автентифікація
		По-перше, якщо ви використовуєте типову (веб-програму), вам потрібно, щоб ваші користувачі пройшли автентифікацію .
		Це означає, що ваша програма має перевірити, чи є користувач тим, за кого себе видає, зазвичай це робиться за 
		допомогою перевірки імені користувача та пароля.
		
			Користувач : "Я президент Сполучених Штатів. Моє username: president!"
			Ваш веб-додаток : "Звичайно, який password у вас, пане президенте?"
			Користувач : «Мій пароль: super secret».
			Ваша веб-програма : "Правильно. Ласкаво просимо, сер!"		
			
			
	2. Авторизація
		У простіших програмах автентифікації може бути достатньо: щойно користувач проходить автентифікацію, він може 
		отримати доступ до кожної частини програми.
		
		Але більшість програм мають концепцію дозволів (або ролей). Уявіть собі: клієнти, які мають доступ до загальнодоступного 
		інтерфейсу вашого веб-магазину, та адміністратори, які мають доступ до окремої адміністративної області.
		
		Обидва типи користувачів мають увійти, але сам факт автентифікації нічого не говорить про те, що їм дозволено 
		робити у вашій системі. Отже, вам також потрібно перевірити дозволи автентифікованого користувача, тобто вам 
		потрібно авторизувати користувача.
		
			Користувач : «Дайте мені пограти в той ядерний футбол...».
			Ваш веб-додаток : «Секундочку, мені потрібно перевірити ваш permissions ..так, пане президенте, у вас правильний рівень дозволу. Насолоджуйтесь».
			Користувач : "Що це за червона кнопка знову...??"	
			
	
	3. Фільтри сервлетів
		Навіщо використовувати фільтри сервлетів?
			будь-яка веб-програма Spring — це лише один сервлет: старий добрий DispatcherServlet Spring , який 
			перенаправляє вхідні HTTP-запити (наприклад, із браузера) на ваші @Controllers або @RestControllers.
			
			Справа в тому, що в цьому DispatcherServlet немає жорстко закодованого захисту, і ви, ймовірно, не захочете 
			возитися з необробленим заголовком HTTP Basic Auth у своїх @Controllers. В оптимальному варіанті 
			автентифікацію та авторизацію слід виконувати до того , як запит потрапить на ваш @Controllers.
			
			На щастя, у веб-світі Java є спосіб зробити саме це: ви можете розмістити фільтри перед сервлетами, що означає, 
			що ви можете подумати про написання SecurityFilter і налаштувати його у своєму Tomcat (контейнер сервлетів/сервер додатків) 
			для фільтрації кожного вхідного HTTP-запит до того, як він потрапить на ваш сервлет.
			
			
			
public class SecurityServletFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        UsernamePasswordToken token = extractUsernameAndPasswordFrom(request);  // (1)

        if (notAuthenticated(token)) {  // (2)
            // either no or wrong username/password
            // unfortunately the HTTP status code is called "unauthorized", instead of "unauthenticated"
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTP 401.
            return;
        }

        if (notAuthorized(token, request)) { // (3)
            // you are logged in, but don't have the proper rights
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // HTTP 403
            return;
        }

        // allow the HttpRequest to go to Spring's DispatcherServlet
        // and @RestControllers/@Controllers.
        chain.doFilter(request, response); // (4)
    }

    private UsernamePasswordToken extractUsernameAndPasswordFrom(HttpServletRequest request) {
        // Either try and read in a Basic Auth HTTP Header, which comes in the form of user:password
        // Or try and find form login request parameters or POST bodies, i.e. "username=me" & "password="myPass"
        return checkVariousLoginOptions(request);
    }


    private boolean notAuthenticated(UsernamePasswordToken token) {
        // compare the token with what you have in your database...or in-memory...or in LDAP...
        return false;
    }

    private boolean notAuthorized(UsernamePasswordToken token, HttpServletRequest request) {
       // check if currently authenticated user has the permission/role to access this request's /URI
       // e.g. /admin needs a ROLE_ADMIN , /callcenter needs ROLE_CALLCENTER, etc.
       return false;
    }
}


	1. По-перше, фільтр повинен отримати ім’я користувача/пароль із запиту. Це може бути через HTTP-заголовок Basic Auth , 
		або поля форми, або файл cookie тощо.
	2. Потім фільтр має перевірити цю комбінацію імені користувача та пароля з чимось, наприклад з даними з бази даних.
	3. Після успішної автентифікації фільтр має перевірити, чи має користувач доступ до запитуваного URI.
	4. Якщо запит витримує всі ці перевірки, тоді фільтр може дозволити запиту пройти до вашого DispatcherServlet, тобто 
		ваших @Controllers.
	
	
	
	
	Ланцюги фільтрів
		хоча наведений вище код працює компілюється, рано чи пізно це призведе до одного чудового фільтра з тонною коду 
		для різних механізмів автентифікації та авторизації.
		
		Однак у реальному світі ви б розділили цей один фільтр на кілька фільтрів, які потім з’єднали .
		
		Наприклад, вхідний запит HTTP…
			Спочатку пройдіть через LoginMethodFilter…
			Потім пройдіть через AuthenticationFilter…
			Потім пройдіть через AuthorizationFilter…
			Нарешті, натисніть свій сервлет.
		
		Ця концепція називається FilterChain , і останній виклик методу у вашому фільтрі вище насправді делегує цьому самому ланцюжку:
					chain.doFilter(request, response);		
	
	
	Аналізуємо Spring's FilterChain
		> BasicAuthenticationFilter : намагається знайти HTTP-заголовок базової автентифікації в запиті та, якщо знайдено, 
			намагається автентифікувати користувача за допомогою імені користувача та пароля заголовка.
		
		> UsernamePasswordAuthenticationFilter : намагається знайти параметр запиту імені користувача/паролю/тіло POST і, 
			якщо знайдено, намагається автентифікувати користувача за допомогою цих значень.
		
		> DefaultLoginPageGeneratingFilter : створює для вас сторінку входу, якщо ви явно не вимкнете цю функцію. 
			ЦЕЙ фільтр є причиною того, чому ви отримуєте сторінку входу за умовчанням, коли вмикаєте Spring Security.
		
		> DefaultLogoutPageGeneratingFilter : створює для вас сторінку виходу, якщо ви явно не вимкнете цю функцію.
		
		> FilterSecurityInterceptor : виконує вашу авторизацію.
	
	
	
	
	Як налаштувати Spring Security: WebSecurityConfigurerAdapter
		З останніми версіями Spring Security та/або Spring Boot спосіб налаштувати Spring Security полягає в наявності класу, який:
		
		Позначено @EnableWebSecurity.
		
		Розширює WebSecurityConfigurer, який в основному пропонує вам конфігурацію DSL/методи. За допомогою цих методів 
		ви можете вказати, які URI у своїй програмі захищати або які засоби захисту від експлойтів увімкнути/вимкнути.
		
		Ось як виглядає типовий WebSecurityConfigurerAdapter:

				@Configuration
				@EnableWebSecurity // (1)
				public class WebSecurityConfig extends WebSecurityConfigurerAdapter { // (1)
				
				  @Override
				  protected void configure(HttpSecurity http) throws Exception {  // (2)
					  http
						.authorizeRequests()
						  .antMatchers("/", "/home").permitAll() // (3)
						  .anyRequest().authenticated() // (4)
						  .and()
					   .formLogin() // (5)
						 .loginPage("/login") // (5)
						 .permitAll()
						 .and()
					  .logout() // (6)
						.permitAll()
						.and()
					  .httpBasic(); // (7)
				  }
				}
				
				
		1) Звичайна Spring @Configuration з анотацією @EnableWebSecurity, що походить від WebSecurityConfigurerAdapter.

		2) Перевизначаючи метод configure(HttpSecurity) адаптера, ви отримуєте гарний маленький DSL, за допомогою 
			якого можна налаштувати FilterChain.
		
		3) Усі запити, що надходять до /та /home, дозволені (дозволені) – користувач не має проходити автентифікацію. 
			Ви використовуєте antMatcher , що означає, що ви також могли використовувати символи підстановки (*, \*\*, ?) у рядку.
		
		4) Будь-який інший запит вимагає, щоб користувач спочатку пройшов автентифікацію , тобто користувач повинен увійти.
		
		5) Ви дозволяєте вхід через форму (ім’я користувача/пароль у формі) за допомогою спеціальної сторінки входу 
			( /login, тобто не автоматично створеної Spring Security). Будь-хто повинен мати доступ до сторінки входу 
			без попереднього входу в систему (permitAll; інакше ми мали б Catch-22!).
		
		6) Те саме стосується сторінки виходу
		
		7) Крім того, ви також дозволяєте базову автентифікацію, тобто надсилання заголовка базової автентифікації 
			HTTP для автентифікації.
	
	
	
	
	Автентифікація за допомогою Spring Security
		Що стосується автентифікації та Spring Security, у вас є приблизно три сценарії:
		
		За замовчуванням : ви можете отримати доступ до (хешованого) пароля користувача, оскільки його дані 
			(ім’я користувача, пароль) збережено, наприклад, у таблиці бази даних.
		
		Рідше : Ви не можете отримати доступ до (хешованого) пароля користувача. Це стосується випадків, коли ваші 	
			користувачі та паролі зберігаються в іншому місці , наприклад, у сторонньому продукті для керування 
			ідентифікацією, який пропонує послуги REST для автентифікації. Подумайте: Atlassian Crowd .
		
		Також популярно : ви хочете використовувати OAuth2 або «Вхід через Google/Twitter тощо». (OpenID), ймовірно, 
			у поєднанні з JWT. Тоді нічого з наведеного нижче не стосується, і вам слід перейти безпосередньо до розділу OAuth2 .
						
 */


/*
Encryption, Encoding, and Hashing

Шифрування (Encryption), кодування (Encoding) та хешування (Hashing) – це схожі терміни, які часто плутають один з одним. Тож давайте обговоримо кожен термін і чим вони відрізняються один від одного.

	Encoding = Кодування:
		у методі кодування дані перетворюються з однієї форми в іншу. Основною метою кодування є перетворення даних у форму, 
		яку читає більшість систем або яку може використовувати будь-який зовнішній процес.
		
		Його не можна використовувати для захисту даних, для кодування використовуються різні загальнодоступні алгоритми.
		
		Кодування можна використовувати для зменшення розміру аудіо- та відеофайлів. Кожен формат аудіо- та відеофайлу має 
		відповідну програму-кодер-декодер (кодек), яка використовується для кодування у відповідний формат, а потім декодує 
		для відтворення.
		
		Приклад: ASCII, BASE64, UNICODE
		
		
	Encryption = Шифрування:
		Це особливий тип кодування, який використовується для передачі приватних даних, наприклад, надсилання комбінації 
		імені користувача та пароля через Інтернет для входу електронною поштою.
		
		Під час шифрування дані, які потрібно зашифрувати (так званий plain-text), перетворюються за допомогою алгоритму 
		шифрування, такого як шифрування AES або шифрування RSA, із використанням секретного ключа, який називається шифром. 
		Зашифровані дані називаються зашифрованим текстом, і, нарешті, секретний ключ може бути використаний одержувачем 
		для перетворення їх назад у звичайний текст.
		
		Існує два типи алгоритмів шифрування – симетричне та асиметричне шифрування.
		У разі симетричного шифрування дані кодуються та декодуються за допомогою одного ключа, наприклад, алгоритму 
		шифрування AES, але у випадку асиметричного алгоритму шифрування дані шифруються за допомогою двох ключів, 
		а саме відкритого та закритого ключів, наприклад. Алгоритм RSA


	Hashing = Хешування:
		Хешування — це односторонній процес, у якому дані перетворюються на буквено-цифровий рядок фіксованої довжини. 
		Цей рядок відомий як хеш або дайджест повідомлення. Хеш не можна повернути до вихідних даних, оскільки це 
		одностороння операція. Хешування зазвичай використовується для перевірки цілісності даних, яке зазвичай 
		називають контрольною сумою. Якщо дві частини ідентичних даних хешуються за допомогою однієї хеш-функції, 
		отриманий хеш буде ідентичним. Якщо два фрагменти даних різні, отримані хеші будуть різними та унікальними.
		
		під час хешування дані перетворюються на хеш за допомогою певної хеш-функції, яка може бути будь-яким числом, 
		згенерованим із рядка чи тексту. Різні алгоритми хешування: MD5, SHA256. Дані після хешування не підлягають обороту.

 */