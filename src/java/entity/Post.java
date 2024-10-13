
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "post")
public class Post  implements Serializable{
    
     @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "description", nullable = false)
    private String description;
    
    @Column(name = "date_time", nullable = false)
    private Date date_time;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    public  Post(){}

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the desc
     */
    public String getDesc() {
        return description;
    }

    /**
     * @param desc the desc to set
     */
    public void setDesc(String desc) {
        this.description = desc;
    }

    /**
     * @return the date_time
     */
    public Date getDate_time() {
        return date_time;
    }

    /**
     * @param date_time the date_time to set
     */
    public void setDate_time(Date date_time) {
        this.date_time = date_time;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    
}
