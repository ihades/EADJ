package org.books.api.api;

/**
 *
 * @author cb
 */
public class GeneratedRestIT {
//
//    private final BooksApi booksApi = new BooksApi();
//    private final CustomersApi customersApi = new CustomersApi();
//    private final OrdersApi ordersApi = new OrdersApi();
//
//    private static Customer customerToRegister;
//
//    private Customer customertToUse;
//
//    @BeforeClass
//    public static void init() {
//        Address address = new Address();
//        address.city = "Bern";
//        address.country = "Switzerland";
//        address.postalCode = "3014";
//        address.street = "Polygonstrasse 32a";
//        CreditCard creditCard = new CreditCard();
//        creditCard.expirationMonth = 12;
//        creditCard.expirationYear = 2020;
//        creditCard.number = "5555555555554444";
//        creditCard.type = CreditCard.TypeEnum.MasterCard;
//        customerToRegister = new Customer();
//        customerToRegister.address = address;
//        customerToRegister.creditCard = creditCard;
//        customerToRegister.email = randomizer() + "@whatever.com";
//        customerToRegister.firstName = "Hans";
//        customerToRegister.lastName = "Wurst";
//    }
//    private String customerNumber;
//
//    @Test(enabled = false)
//    public void bookSearchTest() {
//        for (BookInfo book : booksApi.searchBook("java")) {
//            assertThat(book.isbn, Matchers.notNullValue());
//            assertThat(book.title, Matchers.notNullValue());
//            assertThat(book.price, Matchers.notNullValue());
//            System.out.println("Book " + book.title + " has ISBN " + book.isbn);
//        }
//    }
//
//    @Test
//    public void bookByIsbnTest() {
//        Book book = booksApi.findBook("9781585427659");
//        assertThat(book.authors, Matchers.equalToIgnoringCase("Jeremy Rifkin"));
//        assertThat(book.title, Matchers.equalToIgnoringCase("The Empathic Civilization: The Race to Global Consciousness in a World in Crisis"));
//        assertThat(book.isbn, Matchers.equalToIgnoringCase("1585427659"));
//    }
//
//    @Test(expectedExceptions = {NotFoundException.class})
//    public void bookByWrongIsbnTest() {
//        booksApi.findBook("1234567890");
//    }
//
//    @Test
//    public void registerCustomer() {
//        Registration registration = new Registration();
//        registration.customer = customerToRegister;
//        registration.password = "Start1234";
//        Entity<Registration> e = Entity.entity(registration, MediaType.APPLICATION_JSON_TYPE);
//        System.out.println(e.toString());
//        customerNumber = customersApi.registerCustomer(registration);
//        Assert.assertTrue(!customerNumber.isEmpty());
//    }
//
//    @Test(dependsOnMethods = {"registerCustomer"})
//    public void searchCustomer() {
//        List<CustomerInfo> customers = customersApi.searchCustomer(customerToRegister.firstName);
//        Assert.assertTrue(!customers.isEmpty());
//    }
//
//    @Test(dependsOnMethods = {"registerCustomer"})
//    public void getCustomer() {
//        customertToUse = customersApi.findCustomer(customerNumber);
//        Assert.assertEquals(customerToRegister, customertToUse);
//    }
//
//    @Test(dependsOnMethods = {"registerCustomer"})
//    public void placeOrder() {
//        List<OrderItem> items = new ArrayList<>();
//        OrderItem item1 = new OrderItem();
//        item1.bookInfo = booksApi.searchBook("9781585427659").get(0);
//        item1.quantity = 3;
//        items.add(item1);
////        CustomerInfo customer = new CustomerInfo();
////        customer.email = customerToRegister.email;
////        customer.firstName = customerToRegister.firstName;
////        customer.lastName = customerToRegister.lastName;
////        customer.number = customerNumber;
////        Order order = new Order();
////        order.address = customerToRegister.address;
////        order.creditCard = customerToRegister.creditCard;
////        order.customerInfo = customer;
////        order.date = new Date();
////        order.amount = new Double(50);
////        order.items = items;
//        OrderRequest request = new OrderRequest();
//        request.customerNr = customerNumber;
//        request.items = items;
//        Order order = ordersApi.placeOrder(request);
//
//        Assert.assertNotNull(order);
//    }
//
//    private static String randomizer() {
//        return Double.toString(Math.random() * 10000);
//    }
}
