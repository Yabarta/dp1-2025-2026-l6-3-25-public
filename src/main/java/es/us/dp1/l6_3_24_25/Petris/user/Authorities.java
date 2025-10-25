package es.us.dp1.l6_3_24_25.Petris.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import es.us.dp1.l6_3_24_25.Petris.model.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "authorities")
public class Authorities extends BaseEntity{

//	@ManyToOne
//	@JoinColumn(name = "username")
//	User user;

//	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	String authority;


}
