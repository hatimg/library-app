package com.assignment.library;

import com.assignment.library.rest.LibraryController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LibraryMgmtAppApplicationTests {

	@Autowired
	private LibraryController libraryController;

	@Test
	void contextLoads() {
		Assertions.assertNotNull(libraryController);
	}

}
