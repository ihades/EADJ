package org.books.ejb.dto;

public class CustomerDTO {

    private String email;
    private String firstName;
    private String lastName;
    private String number;
    private AddressDTO address;
    private CreditCardDTO creditCard;

    public CustomerDTO() {
    }

    public CustomerDTO(String email, String firstName, String lastName, String number, AddressDTO address, CreditCardDTO creditCard) {
        this.email = email;
        this.number = number;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.creditCard = creditCard;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public AddressDTO getAddress() {
        if (address == null) {
            address = new AddressDTO();
        }
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public CreditCardDTO getCreditCard() {
        if (creditCard == null) {
            creditCard = new CreditCardDTO();
        }
        return creditCard;
    }

    public void setCreditCard(CreditCardDTO creditCard) {
        this.creditCard = creditCard;
    }
}
