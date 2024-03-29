package kr.or.bit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class Library  {
	
	Admin admin;
	private static File fileAddress = new File("C:\\test\\BitLibrary");; // 파일위치, 없다면 생성
	private static ArrayList<User> userList = null; // 유저리스트
	private static HashMap<Integer, String> bookList = null; // isbn , book
	private String today; // 현재시간
	

	public Library() throws ClassNotFoundException, IOException {
		admin = new Admin();
	}

	
	// 도서관 시작
	public void begin() throws ClassNotFoundException, IOException {

		while (true) {
			System.out.println("──────── 별마당도서관에 오신것을 환영합니다 ───────");
			System.out.println("   ┌┐    ┌┐    ┌┐    ┌┐    ┌┐    ┌┐      ");
			System.out.println("   └┘    └┘    └┘    └┘    └┘    └┘     ");
			System.out.println("─────────────────────────────────────────");
			System.out.println("       원하는 항목을 선택해 주세요                      ");
			System.out.println("");
			System.out.println("   0.프로그램 종료     1.로그인     2.회원등록  " );
			int operation = getInt("", new Scanner(System.in));
			if (operation == 1) {
				System.out.println("");
				System.out.println("원하는 항목을 선택하세요");
				System.out.println("─────────────────────────────────────");
				int useroradmin = getInt("  0.종료     1.회원로그인     2.관리자로그인    ", new Scanner(System.in));
				if (useroradmin == 1) {
					this.userLogin(); // 유저로그인 메소드
					break;
				} else if (useroradmin == 2) {
					this.adminlogin(); // 어드민로그인 메소드
				} else if (useroradmin == 0) {
					saveFile(); // 파일저장
					break; // 종료
				} else {
					System.out.println("잘못된 값을 입력하셨습니다.");
				}
			} else if (operation == 2) {
				this.register(); // 회원등록 메소드
			} else if (operation == 0) {
				saveFile(); // 파일저장
				break; // 종료
			} else {
				System.out.println("잘못된 값을 입력하셨습니다.");
			}
		}
	    System.out.println("────────────────────────────────────");
	}

	// 로그인 메소드
	public void userLogin() throws ClassNotFoundException, IOException {
		// 로그인 콘솔창
		System.out.println("");
		System.out.println("● 회원로그인  ─────────────────────────");
		while (true) {
			System.out.println("핸드폰번호 형식  ex)010-0000-0000");
			System.out.println("──────────────────────────────────");
			String cellNum = getString("핸드폰번호를 입력하세요.", new Scanner(System.in));
			String name = getString("이름을 입력하세요.", new Scanner(System.in));
			// 로그인 데이터 확인
			boolean access = false; // 허용여부
			for (User user : getUserList()) {
				if (cellNum.equals(user.getCellNum())) {
					if (name.equals(user.getName())) {
						access = true; // 아이디 비밀번호 맞으면 허용값 true
						setDate(); // 시간 set
						System.out.println("┌───────────────────────────────┐");
						System.out.println("   " + name + "님 환영합니다\t");
						System.out.println("   " + "접속날짜 :" + today);
						System.out.println("└───────────────────────────────┘");
						System.out.println("");
						userloginyes(user); // 로그인
						break;
					}
				}
			}
			if (!access) { // 아이디가 없을때 false일때
				// 로그인 에러를 리턴
				int loginerrorreturn = this.loginError();
				if (loginerrorreturn == 1) {
					break;
				}
			} else {
				break;
			}
		}
	}
	
	public void adminlogin() throws ClassNotFoundException, IOException {
		// 로그인 콘솔창
		System.out.println("");
		System.out.println("● 관리자로그인  ──────────────────────────");
		while (true) {
			String id = getString("아이디를 입력하세요", new Scanner(System.in));
			String pw = getString("비밀번호를 입력하세요", new Scanner(System.in));
			// 로그인 데이터 확인
			boolean access = false;
			if (id.equals(admin.ID)) {
				if (pw.equals(admin.PW)) {
					access = true; // 아이디 비밀번호 맞으면 허용값 true
					setDate();
					System.out.println("┌───────────────────────────────┐");
					System.out.println("    관리자모드로 로그인하셨습니다.\t");
					System.out.print("    " + "접속날짜 :" + today + "\n");
					System.out.println("└───────────────────────────────┘");
					adminloginyes(admin); // 로그인
					break;
				}
			} else {
				System.out.println("관리자 아이디와 비밀번호 입력을 확인해주세요. ");
				break;
			}

			if (!access) { // 아이디가 없을때 false일때
				// 로그인 에러를 리턴
				int loginerrorreturn = this.loginError();
				if (loginerrorreturn == 1) {
					break;
				}
			} else {
				break;
			}
		}
	}

	
	// 회원등록
	public void register() throws ClassNotFoundException, IOException {
		System.out.println("● 회원등록  ──────────────────────────");
		String registercellNum;
		while (true) {
			System.out.println("핸드폰번호 형식  ex)010-0000-0000");
			System.out.println("맨 앞 자리는 010/011/016/017/018/019만 가능");
			registercellNum = getString("핸드폰번호를 입력해 주세요", new Scanner(System.in));
			String cellformat = "^01(?:0|1[6-9])[-](\\d{3}|\\d{4})[-](\\d{4})$"; // 정규표현식
			boolean cellNumExist = false; // 회원 존재 여부
			for (User user : getUserList()) {
				if (user.getCellNum().equals(registercellNum)) {
					cellNumExist = true; // 핸드폰번호가 있다면
					break;
				}
			}
			if (!registercellNum.matches(cellformat)) {
				System.out.println("잘못입력하셨습니다.");
				System.out.println("핸드폰번호 형식  ex)010-0000-0000");
				System.out.println("맨 앞 자리는 010/011/016/017/018/019만 가능");
				return;
			} // 번호 형식이 다르다면 나오는 이프문

			if (cellNumExist) { // 유저가 존재한다
				System.out.println("동일한 핸드폰번호가 존재합니다");
			} else {
				break; // 존재하지 않는다면 while문 탈출
			}
		}

		// 회원이름 입력
		String registerName = getString("이름을 입력해 주세요", new Scanner(System.in));
		String nameformat = "^[a-zA-Z가-힣]*$"; // 이름 정규표현식
		if (!registerName.matches(nameformat)) {
			System.out.println("잘못된 형식을 입력하셨습니다.");
			System.out.println("알파벳 혹은 한글로 입력해주세요.");
			return;
		} else {
			getUserList().add(new User(registercellNum, registerName)); // 맞다면 회원리스트에 등록
			saveFile();
			System.out.println(registerName + "님 회원가입이 완료되었습니다.");
			System.out.println("");
		}

	}

	public void userloginyes(User user) throws IOException, ClassNotFoundException {

		while (true) {
			System.out.println("● 회원기능  ──────────────────────────");
			System.out.println("1.도서목록                                        ");
			System.out.println("2.대출목록                                        ");
			System.out.println("3.대출                                                       ");
			System.out.println("4.반납                                                       ");
			System.out.println("5.회원탈퇴                                                       ");
			System.out.println("0.로그아웃                                                       ");
			int operationnum = getInt("", new Scanner(System.in));
			if (operationnum == 0) { // 로그아웃
				System.out.println("정상적으로 로그아웃 되었습니다.");
				System.out.println("이용해주셔서 감사합니다.");
				break;
			} else if (operationnum == 5) { // 회원탈퇴
				accountCancellation(user);
				break;
			}
			this.operationnum(user, operationnum); // 1,2,3,4메소드 함수 호출
		}
	}

	public void adminloginyes(Admin admin) throws IOException, ClassNotFoundException {
		System.out.println("");

		admin.start();  // 어드민이라면 어드민클래스로 이동
	}

	
	// 로그인 에러 메소드
	public int loginError() throws ClassNotFoundException, IOException {
		System.out.println("");
		System.out.println("잘못입력하셨습니다.");
		int x = 0;
		System.out.println("────────────────────────────────────");
		System.out.println("1. 재입력                                                  ");
		System.out.println("2. 회원가입                                                ");
		System.out.println("3. 프로그램 종료");
		int operationerror = getInt("", new Scanner(System.in));
		switch (operationerror) {
		case 1:
			break; // 재입력
		case 2:
			register(); // 회원등록
			break;
		case 3: 
			x = 1; // 종료
		default:
			System.out.println("잘못된 번호를 입력하셨습니다.");
			break;
		}
		return x;
	}

	
	// 자세한 조작방법
	public void operationnum(User user, int operationnum) throws IOException, ClassNotFoundException {
		// 자세한 조작 switch
		switch (operationnum) {
		case 1: // 도서목록 보기
			showBookList();
			break;
		case 2:
			// 대출목록 보기
			System.out.println("");
			showBorrowBookList(user);
			break;
		case 3:
			// 대출
			showBookList();
			borrowBook(user);
			break;
		case 4:
			// 반납
			giveBack(user);
			break;
		default:
			System.out.println("정확한 숫자를 입력해주세요");
			break;
		}
	}

	
	private void showBookList() throws IOException, ClassNotFoundException {
		Set<Map.Entry<Integer, String>> bookListSet = getBookList().entrySet(); // 북리스트 set으로 받아옴
		if (bookListSet.size() != 0) { // 사이즈가 0이 아니라면
			System.out.println("도서목록");

		    System.out.println("────────────────────────────────────");
	  	    System.out.println("isbn     도서명");

			for (Entry<Integer, String> entry : bookListSet) { // 총 책 리스트 보여주기
	              System.out.println(" " + entry.getKey() + "      " + entry.getValue());
			}
		
		    System.out.println("────────────────────────────────────");
			System.out.println("현재 우리 도서관의 총 도서 수는 [" + bookListSet.size() + "]개 입니다."); // 총 책 개수
			System.out.println("");

		} else {
			System.out.println("현재 도서관에 대여가능한 도서가 없습니다.");
		}
	}

	
	private void showBorrowBookList(User user) { // 유저가 빌린 책 리스트
		Set<Map.Entry<Integer, String>> borrowBookSet = user.getBorrowBook().entrySet();
		if (borrowBookSet.size() != 0) {
     	   System.out.println("대출하신 도서 목록  ");
		    System.out.println("────────────────────────────────────");
		    System.out.println("isbn     도서명");
			for (Entry<Integer, String> entry : borrowBookSet) {
              System.out.println(" " + entry.getKey() + "      " + entry.getValue());
			}
			
          System.out.println("");
		} else {
          System.out.println(user.getName() + "님의 도서 대여 목록이 없습니다.");
          System.out.println("");
		}
	}


	public void borrowBook(User user) throws IOException, ClassNotFoundException { // 대출

		int booki = getInt("원하는 책 번호를 입력해주세요", new Scanner(System.in));
		while (true) {
			HashMap<Integer, String> bookList = getBookList();
			if (bookList.get(booki) != null) {
				System.out.println("");
				System.out.printf("%s님 도서 [%s]가 정상적으로 대출 되었습니다.\n", user.getName(), bookList.get(booki));
				System.out.println("");
				user.getBorrowBook().put(booki, bookList.get(booki));
				bookList.remove(booki);
				saveFile();			
				break;

			} else {
				System.out.println("알맞는 도서 번호를 입력하세요");
				break;
			}
		}
	}

	// 반납
	public void giveBack(User user) throws IOException, ClassNotFoundException {
		System.out.println("");
		System.out.println(user.getName()+ "님이" );
		showBorrowBookList(user);
		int booki = getInt("반납할 책 번호를 입력하세요", new Scanner(System.in));
		while (true) {
			if (user.getBorrowBook().get(booki) != null) {
				System.out.println("");
				System.out.printf("%s님께서 도서 [%s]의 반납을 완료하셨습니다.\n", user.getName(), user.getBorrowBook().get(booki));
				getBookList().put(booki, user.getBorrowBook().get(booki));
				user.getBorrowBook().remove(booki);
				saveFile();
				break;

			} else {
				System.out.println("알맞는 도서 번호를 입력하세요");
				break;
			}
		}
	}

	
	// 회원탈퇴
	public void accountCancellation(User user) throws ClassNotFoundException, IOException {
		System.out.println("────────────────────────────────────");
		System.out.println("탈퇴 후에는 다시 계정을 생성해야 하며, 대출기록은 사라집니다. 그래도 탈퇴하시겠습니까?");
		System.out.println("1. 확인");
		System.out.println("0. 되돌아가기");
		String operation = getString("원하는 번호는 선택하세요", new Scanner(System.in)); //0 이외의 번호를 누르시면 다시 되돌아 갑니다.
		
		if(!(user.getBorrowBook() == null)) {
			System.out.println("현재 대여중인 도서가 있습니다.");
			System.out.println("반납 후 진행해주세요.");
			System.out.println("");
			userloginyes(user);

			
			if (operation.equals("1")) {
				for (User value : getUserList()) {
					if (user.getName().equals(value.getName())) {
						getUserList().remove(value); // 회원삭제
						saveFile();
						System.out.println("이용해주셔서 감사합니다.");
						break;
					}
				}
			}else {
				userloginyes(user);
				System.out.println("");
			}
		}

	}

	// 파일주소 가져오기
	public static File getFileAddress() {
		if (!fileAddress.isDirectory()) {
			fileAddress.mkdirs();
		}
		return fileAddress;
	}

	

	// 유저 리스트 불러오기
	public static ArrayList<User> getUserList() throws IOException, IOException, ClassNotFoundException {
		if (userList == null) {
			try {
				FileInputStream fisUser = new FileInputStream(new File(fileAddress, "User.txt"));
				BufferedInputStream bisUser = new BufferedInputStream(fisUser);
				ObjectInputStream ooUser = new ObjectInputStream(bisUser);
				Object obj = ooUser.readObject();
				userList = (ArrayList<User>) obj;
				fisUser.close();
				bisUser.close();
				ooUser.close();
			} catch (Exception e) {
				userList = new ArrayList<User>();
			}
		}
		return userList;
	}

	// 책 리스트 불러오기
	public static HashMap<Integer, String> getBookList() throws IOException, IOException, ClassNotFoundException {
		if (bookList == null) { // null이라면 책 리스트 불러온다.
			try {
				FileInputStream fisBook = new FileInputStream(new File(fileAddress, "Book.txt"));
				BufferedInputStream bisBook = new BufferedInputStream(fisBook);
				ObjectInputStream ooBook = new ObjectInputStream(bisBook);
				Object obj = ooBook.readObject();
				bookList = (HashMap<Integer, String>) obj;
				fisBook.close();
				bisBook.close();
				ooBook.close();

			} catch (Exception e) { // 에러가 뜬다면 책을 넣는다.
				bookList = new HashMap<Integer, String>();
				bookList.put(1, "자바의 정석");
				bookList.put(2, "쉽게 배우는 jsp 프로그래밍");
				bookList.put(3, "아플수록 청춘이다");
				bookList.put(4, "평범한 삶");
				bookList.put(5, "보이지 않는 손");
				bookList.put(6, "도박사");
				bookList.put(7, "경영자의 마인드");
				bookList.put(8, "신의 탑");
				bookList.put(9, "김씨표류기");
				bookList.put(10, "오라클로 배우는 데이터베이스 입문");

			}
		}
		return bookList;
	}

	// IO메소드 저장기능
	public static void saveFile() throws IOException, IOException, ClassNotFoundException {
		FileOutputStream fosUser = new FileOutputStream(new File(fileAddress, "User.txt"));
		BufferedOutputStream bosUser = new BufferedOutputStream(fosUser);
		ObjectOutputStream ooUser = new ObjectOutputStream(bosUser);

		FileOutputStream fosBook = new FileOutputStream(new File(fileAddress, "Book.txt"));
		BufferedOutputStream bosBook = new BufferedOutputStream(fosBook);
		ObjectOutputStream ooBook = new ObjectOutputStream(bosBook);

		ooUser.writeObject(getUserList());
		ooBook.writeObject(getBookList());

		fosUser.flush();
		fosBook.flush();

		bosUser.flush();
		bosBook.flush();
		ooUser.flush();
		ooBook.flush();
		fosUser.close();
		fosBook.close();

		bosUser.close();
		bosBook.close();

		ooUser.close();
		ooBook.close();

	}
	
	// 스캐너로 스트링값 받는 메소드
	public static String getString(String i, Scanner sc) {
		System.out.println(i);
		String value = sc.nextLine();
		return value;
	}

	// 스케너로 인트값 받는 메소드
	public static int getInt(String i, Scanner sc) {
		System.out.println(i);
	    System.out.println("────────────────────────────────────");
		System.out.print("───> ");
		while (true) {
			if (sc.hasNextInt()) {
				boolean intFlag = true;
				while (intFlag) {
					int value = sc.nextInt();
					if (value < 0) {
						intFlag = false;
						System.out.println("정확한 값을 입력해주세요");
					} else {
						return value;
					}
				}
			} else {
				System.out.println("정확한 값을 입력해주세요");
				sc = new Scanner(System.in);
			}
		}
	}

	// 현재날짜 만드는 메소드
	public void setDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월dd일 HH시mm분ss초");
		java.util.Date date = new java.util.Date();
		today = sdf.format(date);
	}

}
