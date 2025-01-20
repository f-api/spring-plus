package org.example.expert.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.entity.Timestamped;
import org.example.expert.domain.user.enums.UserRole;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String email;
	private String password;
	@Enumerated(EnumType.STRING)
	private UserRole userRole;
	private String nickname; // Lv1-2: 컬럼에 nickname 추가

	public User(String email, String password, String nickname, UserRole userRole) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.userRole = userRole;
	}

	private User(Long id, String email, UserRole userRole) {
		this.id = id;
		this.email = email;
		this.userRole = userRole;
	}

	public static User fromAuthUser(AuthUser authUser) {
		return new User(authUser.getId(), authUser.getEmail(), authUser.getUserRole());
	}

	public void changePassword(String password) {
		this.password = password;
	}

	public void updateRole(UserRole userRole) {
		this.userRole = userRole;
	}
}
