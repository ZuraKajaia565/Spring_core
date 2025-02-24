package com.zura.gymCRM.service;

import com.zura.gymCRM.model.User;
import java.util.Map;

public class UsernameGenerator {
	public static String generateUsername(String firstName, String lastName,
			Map<Integer, ? extends User> userMap) {
		String userName = firstName + "." + lastName;
		int cnt = 0;
		for (Map.Entry<Integer, ? extends User> entry : userMap.entrySet()) {
			User user = entry.getValue();
			if (user.getFirstName().equals(firstName) &&
					user.getLastName().equals(lastName)) {
				cnt++;
			}
		}
		if (cnt != 0) {
			userName += cnt;
		}
		return userName;
	}
}
