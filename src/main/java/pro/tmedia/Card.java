package pro.tmedia;

/**
 * Created by TIvaykin on 04.03.2017.
 */
public class Card {
    private int id;
    private String name;
    private String description;
    private String conditions;
    private String contacts;
    private String type;
    private String city;
    public enum types {
        grant,
        event,
        debt
    }
    public enum cities {
        khabarovsk
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getType() {
        return type;
    }

    public void setType(types type) {
        switch (type) {
            case debt:
                this.type = "debt";
                break;
            case event:
                this.type = "event";
                break;
            case grant:
                this.type = "grant";
                break;
        }
    }

    public String getCity() {
        return city;
    }

    public void setCity(cities city) {
        switch (city) {
            case khabarovsk:
                this.type = "khabarovsk";
                break;
        }
    }
}
