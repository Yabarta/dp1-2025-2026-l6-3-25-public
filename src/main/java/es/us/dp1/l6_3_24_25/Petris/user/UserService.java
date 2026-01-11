/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.us.dp1.l6_3_24_25.Petris.user;

import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.model.Statistics;
import es.us.dp1.l6_3_24_25.Petris.player.repository.PlayerRepository;
import es.us.dp1.l6_3_24_25.Petris.player.repository.StatisticsRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;

@Service
public class UserService {

    private final StatisticsRepository statisticsRepository;
    private final PlayerRepository playerRepository;
    private UserRepository userRepository;

	public UserService(UserRepository userRepository, StatisticsRepository statisticsRepository, PlayerRepository playerRepository) {
		this.userRepository = userRepository;
        this.statisticsRepository = statisticsRepository;
        this.playerRepository = playerRepository;
    }

	@Transactional
	public User saveUser(User user) throws DataAccessException {
        userRepository.save(user);
        if(user.hasAuthority("PLAYER")){
            Statistics statistics = Statistics.builder().
                gamesPlayed(0).
                gamesWon(0).
                timePlayed(0).
                sarcinasCreated(0).
                bacteriasCreated(0).
                build();

            statisticsRepository.save(statistics);

            Player newPlayer = Player.builder().
                nickname(user.getUsername()).
                email("example@gmail.com").
                profilePicture("").
                isCurrentlyInMatch(false).
                statistics(statistics).
                user(user).
                build();

            playerRepository.save(newPlayer);
        }

		return user;
	}

	@Transactional(readOnly = true)
	public User findUser(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
	}

	@Transactional(readOnly = true)
	public User findUser(Integer id) {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
	}

	@Transactional(readOnly = true)
	public User findCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null)
			throw new ResourceNotFoundException("Nobody authenticated!");
		else
			return userRepository.findByUsername(auth.getName())
					.orElseThrow(() -> new ResourceNotFoundException("User", "Username", auth.getName()));
	}

	public Boolean existsUser(String username) {
		return userRepository.existsByUsername(username);
	}

	@Transactional(readOnly = true)
	public Iterable<User> findAll() {
		return userRepository.findAll();
	}

	public Iterable<User> findAllByAuthority(String auth) {
		return userRepository.findAllByAuthority(auth);
	}

	@Transactional(readOnly = true)
	public Page<User> findAllPaginated(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	@Transactional(readOnly = true)
	public Page<User> findAllByAuthorityPaginated(String auth, Pageable pageable) {
		return userRepository.findAllByAuthority(auth, pageable);
	}

	@Transactional
	public User updateUser(@Valid User user, Integer idToUpdate) {
		User toUpdate = findUser(idToUpdate);
		BeanUtils.copyProperties(user, toUpdate, "id");
		userRepository.save(toUpdate);

		return toUpdate;
	}

	@Transactional
	public void deleteUser(Integer id) {
		User toDelete = findUser(id);
		this.userRepository.delete(toDelete);
	}


}
