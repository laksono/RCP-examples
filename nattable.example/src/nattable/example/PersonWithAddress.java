package nattable.example;


import java.util.Date;

public class PersonWithAddress extends Person {

    private Address address;

    public PersonWithAddress(
            int id,
            String firstName,
            String lastName,
            Gender gender,
            boolean married,
            Date birthday,
            Address address) {
        super(id, firstName, lastName, gender, married, birthday);
        this.address = address;
    }

    public PersonWithAddress(Person person, Address address) {
        super(person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getGender(),
                person.isMarried(),
                person.getBirthday());
        this.address = address;
    }

    /**
     * @param address
     *            the address to set
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * @return the address
     */
    public Address getAddress() {
        return this.address;
    }

}