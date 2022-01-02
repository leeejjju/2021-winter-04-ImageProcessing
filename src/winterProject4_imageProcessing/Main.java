package winterProject4_imageProcessing;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Main {

	/*
	 *정보
	 2021-12-30-17:00~ 2021-01-03-11:00
	 2021-winter 과제4, 간단한 이미지 편집 프로그램 만들기 
	 HGU 전산전자공학부_ 22100579 이진주
	 */
	
	/*
	 *Todo
	 1. 파일에서 선택해 불러온 이미지 화면에 display
	 2. 기능 세가지 선택, 구현
	 	2-1 흑백처리, edge추출, contrast조정, smoothing, 합성, 밝기조절, save/load ...
	 	
	 3. 흑백처리
	 4. contrast조정, 밝기조정 등 
	 5. save/load 
	 */
	
	//누르고있는동안만 원본 보여주기? 원본패널을 만들어놓고 마우스이벤트에 따라 visibility 조절하자 
	
	
	static JFrame frame;
	static JPanel BB; //블랙보드라는 뜻임ㅋㅋ 
	static JPanel canvas; //사진 띄우고 보여줄 영역 
	static JFrame forB = new JFrame(); //원본보기 버튼만 뿅 띄워둘 패널
	static CardLayout up = new CardLayout(); //카드레이아웃.. 
	static JPanel tools; //툴바
	static Font font = new Font("Adobe Fan Heiti Std", Font.BOLD, 15);
	static JFrame pop = new JFrame("Finder"); //파일선택시 띄울 창 
	
	static JFrame sliderTab; //ㅠㅠ밝기랑 이것저것 조절할때 추가로 뜰 창... 
	
	static boolean on = false; //이미지 편집 시작 (버튼작동 트리거)
	
	
	//기본프레임과 툴바 
	public static void makeFrame() {
		
		//기본껍데기 작업
		frame = new JFrame("Image Processing ^^*"); //새로운 프레임 생성 
		frame.setSize(1080, 720); //프레임의 사이즈 설정
		frame.setResizable(false);//사용자가 임의로 프레임의 크기를 변경시킬 수 있는가>> 앙대
		frame.setLocationRelativeTo(null);//화면의 어느 위치에서 첫 등장할지>> null이면 자동 센터지정 
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//프레임 닫으면 프로그램도 같이 종료.
		
		BB = new JPanel(); //기본적으로 씌워둘 패널 추가
		BB.setLayout(null); //레이아웃으로 설정 
		frame.add(BB); //프레임에 넣기 
		
		tools = new JPanel();  //도구?버튼?모음
		tools.setBounds(800, 0, 280, 720);
		tools.setLayout(null); //레이아웃
		tools.setBackground(Color.DARK_GRAY);
		BB.add(tools);
		
		//캔버스영역?지정 
		canvas = new JPanel();
		canvas.setBounds(0, 0, 800, 720);
		canvas.setLayout(up);
		canvas.setBackground(Color.BLACK);
		BB.add(canvas);
		
		
		
		//파일열기
		JButton getFile = new JButton("Import image");
		getFile.setBackground(Color.LIGHT_GRAY);
		getFile.setFont(font);
		getFile.setBounds(30, 40, 225, 50);
		tools.add(getFile);
		
		
		getFile.addActionListener(event->{
			
			File selectedFile;
			JFileChooser finder = new JFileChooser("C:\\Java\\workspace\\winterProject4_imageProcessing\\images\\");
			//인자로 넣어준 String으로 디폴트 디렉토리 지정 
			int result = finder.showOpenDialog(pop); 
			//정상적으로 파일이 열리면 0 반환, 취소누르면 1 반환
			if(result == JFileChooser.APPROVE_OPTION) { //선택파일이 정상적으로 열렸을떄
				selectedFile = finder.getSelectedFile(); //선택한 파일의 경로 저장 
				System.out.println("성공적으로 불러왔습니다");
				Images.loadImage(selectedFile); //오리진이미지 저장하고 띄워주는 버튼추가 
				on = true; //파일열렸어용~ 알려주는놈(버튼작동트리거)
			}else {
				System.out.println("파일 안열림");
			}
		});
		
		
		//원본보기(버튼 누르고있는 동안만 원본 사진 보이게 하기 )
		JButton viewOrigin = new JButton("View origin");
		viewOrigin.setBackground(Color.GRAY);
		viewOrigin.setBounds(30, 110, 100, 50);
		tools.add(viewOrigin);
		viewOrigin.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) { 
				if(on) {
					Main.up.show(Main.canvas, "origin"); //원본사진 패널이 위로가게
				}
	        }
			public void mouseReleased(MouseEvent e) { 
				if(on) {
					Main.up.show(Main.canvas, "edited"); //편집중사진 패널이 위로가게 
				}
	        }
		});
		
		//원본으로 되돌리기
		JButton backToOrigin = new JButton("Back to origin");
		backToOrigin.setBackground(Color.LIGHT_GRAY);
		backToOrigin.setBounds(135, 110, 120, 50);
		tools.add(backToOrigin);
		backToOrigin.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				if(on) {
					Images.backUpIMG = Images.outputIMG; //백업
				}
			}
		});
		backToOrigin.addActionListener(event->{
			if(on) {
				Images.outputIMG = Images.inputIMG; 
				Images.savedIMG = Images.inputIMG; 
				Images.editIMG();
			}
		});

	
		//밝기조정
		JButton B = new JButton("Brightness");
		B.setBackground(Color.LIGHT_GRAY);
		B.setFont(font);
		B.setBounds(30, 180, 225, 50);
		tools.add(B);
		B.addActionListener(event->{
			if(on) {
				makeSlider('B');
			}
		});
		B.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				if(on) {
					Images.backUpIMG = Images.outputIMG; 
				}
			}
		});
		
		//채도 조정
		JButton S = new JButton("Saturation");
		S.setBackground(Color.LIGHT_GRAY);
		S.setFont(font);
		S.setBounds(30, 240, 225, 50);
		tools.add(S);
		S.addActionListener(event->{
			if(on) {
				makeSlider('S');
			}
		});
		S.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				if(on) {
					Images.backUpIMG = Images.outputIMG; 
				}
			}
		});
		
		//명암대비 조정
		JButton C = new JButton("Contrast");
		C.setBackground(Color.LIGHT_GRAY);
		C.setFont(font);
		C.setBounds(30, 300,  225, 50);
		tools.add(C);
		C.addActionListener(event->{
			if(on) {
				makeSlider('C');
			}
		});
		C.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				if(on) {
					Images.backUpIMG = Images.outputIMG; 
				}
			}
		});
		
		
		//흑백처리
		JButton mono = new JButton("Cange to black&white");
		mono.setBackground(Color.LIGHT_GRAY);
		mono.setFont(font);
		mono.setBounds(30, 370, 225, 50);
		tools.add(mono);
		mono.addActionListener(event->{ //기능
			if(on) {
				Images.changeToMono();
				Images.apply();
			}
		});
		mono.addMouseListener(new MouseAdapter(){ //백업처리
			public void mousePressed(MouseEvent e) {
				if(on) {
					Images.backUpIMG = Images.outputIMG;
				}
			}
		});
		
		
		//색반전
		JButton revers = new JButton("Cange to color revers");
		revers.setBackground(Color.LIGHT_GRAY);
		revers.setFont(font);
		revers.setBounds(30, 430, 225, 50);
		tools.add(revers);
		revers.addActionListener(event->{ //기능
			if(on) {
				Images.changeToRevers();
				Images.apply();
			}
		});
		revers.addMouseListener(new MouseAdapter(){ //백업처리
			public void mousePressed(MouseEvent e) {
				if(on) {
					Images.backUpIMG = Images.outputIMG;
				}
			}
		});
		
		
		
		//뒤로가기(딱한번, 스택처리하면 여려번도 가능할듯 ) - 백업된 머시깽이로 되돌리기 
		JButton undo = new JButton("Undo");
		undo.setBackground(Color.LIGHT_GRAY);
		undo.setFont(font);
		undo.setBounds(30, 500, 110, 50);
		tools.add(undo);
		
		undo.addActionListener(event->{
			if(on) {
				Images.outputIMG = Images.backUpIMG;
				Images.apply();
				Images.editIMG();
			}
		});
		
		//앞으로가기
		JButton redo = new JButton("Redo");
		redo.setBackground(Color.LIGHT_GRAY);
		redo.setFont(font);
		redo.setBounds(145, 500, 110, 50);
		tools.add(redo);
		
		redo.addActionListener(event->{
			if(on) {
				Images.outputIMG = Images.backUpIMG;
				Images.apply();
				Images.editIMG();
			}
		});
		
		
		//파일 저장
		JButton saveFile = new JButton("Export Image");
		saveFile.setBackground(Color.LIGHT_GRAY);
		saveFile.setFont(font);
		saveFile.setBounds(30, 570, 225, 50);
		tools.add(saveFile);
		
		saveFile.addActionListener(event->{
			if(on) {
				File pathForSave; //저장할 위치
				JFileChooser finder = new JFileChooser("C:\\Java\\workspace\\winterProject4_imageProcessing\\images\\");
				finder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				//인자로 넣어준 String으로 디폴트 디렉토리 지정 
				int result = finder.showOpenDialog(pop); 
				//정상적으로 파일이 열리면 0 반환, 취소누르면 1 반환
				if(result == JFileChooser.APPROVE_OPTION) { //선택파일이 정상적으로 열렸을떄
					pathForSave = finder.getSelectedFile(); //선택한 파일의 경로 저장 
			    	 try{
			    		 ImageIO.write(Images.EDITED, "jpg", pathForSave); // write메소드를 이용해 파일을 만든다
				         System.out.println("저장되었습니다");
			         }catch(Exception e){
			            e.printStackTrace();
			            System.out.println("저장에 실패했습니다.");
			         }    
				}else {
					System.out.println("저장에 실패했습니다.");
				}
			}
		});
		
		JLabel info = new JLabel("2021-winter | java | project4 | Leeejjju");
		info.setForeground(Color.white);
		info.setBounds(50, 650, 225, 30);
		tools.add(info);
		
		
		
		frame.setVisible(true); //쨘 
		
	}
	
	//슬라이더 관리 
	public static void makeSlider(char state) {
		
		//기본껍데기 작업
		sliderTab = new JFrame(); //새로운 프레임 생성 
		sliderTab.setSize(280, 200); //프레임의 사이즈 설정
		sliderTab.setResizable(false);//사용자가 임의로 프레임의 크기를 변경시킬 수 있는가>> 앙대
		sliderTab.setLocationRelativeTo(null);//화면의 어느 위치에서 첫 등장할지>> null이면 자동 센터지정 
		sliderTab.setLayout(null); //자유배치 위한 레이아웃
		
		//Brightness, 밝기조절 ---------------------------------------------------------------------------------
		JLabel infoB = new JLabel(":: Brightness");
		infoB.setFont(font);
		infoB.setBounds(20, 0, 200, 50);
		sliderTab.add(infoB);
		
		JSlider B = new JSlider(-100, 100, 0);
		B.setBounds(20, 40, 240, 50);
		B.setMajorTickSpacing(40); //큰 눈금 간격 10로 설정
		B.setMinorTickSpacing(0); //작은 눈금 간격 1로 설정
		B.setPaintTicks(true); //눈금을 표시한다.
		B.setPaintLabels(true); //값을 레이블로 표시한다.
		B.setBackground(null); //배경없애기!!!
		sliderTab.add(B);
		
		B.addChangeListener(event->{
			Images.changeBrightness(B.getValue());
		});

		
		//Saturation, 채도조절 ----------------------------------------------------------------------------------
		JLabel infoS = new JLabel(":: Saturation");
		infoS.setFont(font);
		infoS.setBounds(20, 0, 200, 50);
		sliderTab.add(infoS);
		
		JSlider S = new JSlider(-100, 100, 0);
		S.setBounds(20, 40, 240, 50);
		S.setMajorTickSpacing(40); //큰 눈금 간격 10로 설정
		S.setMinorTickSpacing(0); //작은 눈금 간격 1로 설정
		S.setPaintTicks(true); //눈금을 표시한다.
		S.setPaintLabels(true); //값을 레이블로 표시한다.
		S.setBackground(null); //배경없애기!!!
		sliderTab.add(S);
		
		
		S.addChangeListener(event->{
			Images.changeSaturation(S.getValue());
		});
		
		//Contrast, 명암대비 --------------------------------------------------------------------------------------
		JLabel infoC = new JLabel(":: Contrast");
		infoC.setFont(font);
		infoC.setBounds(20, 0, 200, 50);
		sliderTab.add(infoC);
		
		JSlider C = new JSlider(-100, 100, 0);
		C.setBounds(20, 40, 240, 50);
		C.setMajorTickSpacing(40); //큰 눈금 간격 10로 설정
		C.setMinorTickSpacing(0); //작은 눈금 간격 1로 설정
		C.setPaintTicks(true); //눈금을 표시한다.
		C.setPaintLabels(true); //값을 레이블로 표시한다.
		C.setBackground(null); //배경없애기!!!
		sliderTab.add(C);
		
		C.addChangeListener(event->{
			Images.changeContrast(C.getValue());
		});
		
		
		
		//적용 버튼 
		JButton confirm = new JButton("Confirm");
		confirm.setBackground(Color.LIGHT_GRAY);
		confirm.setFont(font);
		confirm.setBounds(40, 105, 180, 40);
		sliderTab.add(confirm);
		
		confirm.addActionListener(event->{
			Images.apply();
			B.setValue(0);
			S.setValue(0);
			C.setValue(0);
			sliderTab.dispose(); //창닫기 
		});

		
		//인자로 들어온 값에 따라 보이게할...그거가 달라짐 
		//카드레이아웃 쓰면 더 깔끔해질듯 근데 수정할여유가엄서 
		if(state == 'B') {
			infoB.setVisible(true);
			B.setVisible(true);
			infoS.setVisible(false);
			S.setVisible(false);
			infoC.setVisible(false);
			C.setVisible(false);
		}else if(state == 'S') {
			infoB.setVisible(false);
			B.setVisible(false);
			infoS.setVisible(true);
			S.setVisible(true);
			infoC.setVisible(false);
			C.setVisible(false);
		}else if(state == 'C') {
			infoB.setVisible(false);
			B.setVisible(false);
			infoS.setVisible(false);
			S.setVisible(false);
			infoC.setVisible(true);
			C.setVisible(true);
		}
		

		sliderTab.setVisible(true); //쨘 
		
	}
	
	//메인함수. 실행할 뿐.
	public static void main(String[] args) {
		System.out.println("Hello world!");
		makeFrame();
		
		
	}

}


// 이미지 처리하기 
class Images {
	
	static BufferedImage ORIGIN; //원본
	static BufferedImage EDITED; //편집본 
	
	static int height; //세로길이
	static int width; //가로길이
	
	static JPanel edited; //편집중 이미지 담을 패널 
	static JLabel e; //편집된 버퍼이미지 담을 라벨 
	
	
	static int[][][] inputIMG; //변경전 각 픽셀들의 rgb값 저장해둘 메모리공간
	static int[][][] backUpIMG; //뒤로가기 실행을 위한 중간저장.. 
	static int[][][] savedIMG; //중간과정 저장! 
	static int[][][] outputIMG = new int[3][height][width];; //변경후 각 픽셀들의 rgb값 저장해둘 메모리공간 
	
	
	
	//원본버튼 추가하고 원본이랑 편집본 패널 추가하고.. 
	public static void loadImage(File file) {
		
		//path는 파일파인더에서 잡아다가 넣어줄 파일경로임!! 이거가지고 띄워줘야대 
		
		/*
		//원본 보여주는 버튼 추가 
		ImageIcon icon = new ImageIcon("C:\\Java\\workspace\\winterProject4_imageProcessing\\originIcon.png");
		JButton viewOrigin = new JButton(icon);
		viewOrigin.setBackground(Color.WHITE);
		viewOrigin.setBounds( 0, 0, 40, 40);
		Main.tools.add(viewOrigin);
		*/
		
		//버퍼이미지에 불러온 파일 저장 
		try {
			ORIGIN = ImageIO.read(file);
			EDITED = ImageIO.read(file);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//이미지 정보 받아오고 리사이즈
		width = ORIGIN.getWidth(); //가로사이즈
		height = ORIGIN.getHeight(); //세로사이즈 
		float ratio =(float)((width<height)?width:height)/((width>height)?width:height) ; //더 긴사이즈 대비 짧은사이즈의 비율 
		System.out.printf("가로사이즈 %d 세로사이즈 %d 비율 %f\n", width, height, ratio);
		
		width = 700;
		height = 700;
		
		/*
		if(width > height) { //프레임 안에 들어가게 사이즈조정..ㅎㅎ 
			width = 700;
			height = (int) (700 * ratio);
		}else {
			width = (int) (700 * ratio);
			height = 700;
		}
		System.out.printf("변경된 가로사이즈 %d 세로사이즈 %d 비율 %f\n", width, height, ratio);
		*/
		
		//사진을 변경된 사이즈로 리사이즈해야됨 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		
		//원본사진 고대로 담을 패널
		JPanel origin = new JPanel();
		origin.setBounds(20, 20, 700,700); //이미지ㅣ 크기 어케받아오누 일단 700*처리 
		origin.setBackground(null);
		JLabel o = new JLabel(new ImageIcon(ORIGIN));
		origin.add(o);
		Main.canvas.add(origin, "origin"); //origin이라는 이름의 카드로 캔버스에 삽입 
		
		//편집중인 사진 담을 패널 
		edited = new JPanel();
		edited.setBounds(20, 20, 700,700); //
		edited.setBackground(null);
		e = new JLabel(new ImageIcon(EDITED));
		edited.add(e);
		Main.canvas.add(edited, "edited"); //edited 라는 이름의 카드로 캔버스에 삽입 
		Main.up.show(Main.canvas, "edited");
		
		/*
		//버튼 누르고있는 동안만 원본 사진 보이게 하기 
		viewOrigin.addMouseListener(new MouseAdapter(){
			
			public void mousePressed(MouseEvent e) { 
				Main.up.show(Main.canvas, "origin"); //원본사진 패널이 위로가게
	        }
			public void mouseReleased(MouseEvent e) { 
				Main.up.show(Main.canvas, "edited"); //편집중사진 패널이 위로가게 
	        }
			
		});
		*/
		
		getMemories(); //원본사진의 각 픽셀 정보 추출하여 inputIMG 메모리에 넣기 
		
		
	}
	
	//밝기조절!! 
	public static void changeBrightness(int std) {
		
		//원본에서 밝기처리한 내용 새 메모리에 저장 
		outputIMG = new int[3][height][width]; //공간만들고.. 
		
		for(int RGB = 0; RGB < 3; RGB++) {
			for(int i = 0; i < height; i++) {
				for(int j = 0; j < width; j++) {
					
					//원본의..뭐시깽이를 가져다가 
					int value = savedIMG[RGB][i][j];
					
					value += std*2;
					
					
					if(value >= 255) {
						value = 255;
					}else if(value <= 0) {
						value = 0;
					}
					
					//새 버퍼이미지 각 픽셀 자리에 너어주기
					outputIMG[RGB][i][j] = value;
				}
			}
		}
		
		editIMG();
		
	}
	
	//명암대비 조절!!
	public static void changeContrast(int std) {
		
		
		//원본에서 밝기처리한 내용 새 메모리에 저장 
		outputIMG = new int[3][height][width]; //공간만들고.. 
	
		for(int RGB = 0; RGB < 3; RGB++) {
			for(int i = 0; i < height; i++) {
				for(int j = 0; j < width; j++) {
					
					//원본의..뭐시깽이를 가져다가 
					int value = savedIMG[RGB][i][j];
					
					//0이면 변화없고
					if(std == 0) {
						return;
					}
					
					//양수면 각 rgb값 강조
					if(std > 0) {
						
						if(value <127) {
							value -= std;
						}else{
							value += std;
						}
						
						if(value >= 255) {
							value = 255;
						}else if(value <= 0) {
							value = 0;
						}
						
					//음수면 각 rgb값을 127(회색)에 가깝게 조정 
					}else {
						
						float STD = 1 - ((float)Math.abs(std)/100); //원본을 가지고갈..비율..?
						
						if(value <127) {
							value = (int) (127 - ((127 - value) * STD));
						}else{
							value = (int) (127 + ((value - 127) * STD));
						}
						
					}

					//새 버퍼이미지 각 픽셀 자리에 너어주기
					outputIMG[RGB][i][j] = value;
				}
			}
		}
		
		editIMG();
		
	}
	
	//채도조절??
	public static void changeSaturation(int std) {
		outputIMG = new int[3][height][width]; //공간만들고.. 
		
		//인풋메모리 참조, 기준 따라 변형하여 아웃풋메모리에 넣기 
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				
				//현재픽셀의 rgb 추출하여
				int R = savedIMG[0][i][j]; //red 
				int G = savedIMG[1][i][j]; //green
				int B = savedIMG[2][i][j]; //blue
				
				float[] HSB = Color.RGBtoHSB(R, G, B, null);
				
				if(std == 0) return;
				float S;
				
				float STD= (Math.abs(std)+20)/20;
				
				
				//채도 더해줌
				if(std > 0) {
					S = HSB[1]*STD; //현재 채도 따서 높여주기 
					if(S > 1) { //최대,,값은 1로 
						S = 1;
					}
				}
				//채도 낮춰줌 
				else {
					S = HSB[1]/STD; //현재 채도 따서 낮춰주기 
					if(S < 0) { //최소,,값은 0으로 
						S = 0;
					}
				}
				
				int RGB = Color.HSBtoRGB(HSB[0], S, HSB[2]);
				
				//변환한 값 넣어주기 근데 이게 맞나? 
				outputIMG[0][i][j] = (RGB>>16)&0xFF; //red
				outputIMG[1][i][j] = (RGB>>8)&0xFF; //green
				outputIMG[2][i][j] = RGB&0xFF; //blue
			}
		}
		
		//편집중 버퍼이미지에 반영하기 
		editIMG();
		
	}
	
	
	
	
	//흑백처리 
	public static void changeToMono() {
		outputIMG = new int[3][height][width]; //공간만들고.. 
		
		//인풋메모리 참조, 기준 따라 변형하여 아웃풋메모리에 넣기 
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				
				//현재픽셀의 rgb 추출하여
				int R = savedIMG[0][i][j]; //red 
				int G = savedIMG[1][i][j]; //green
				int B = savedIMG[2][i][j]; //blue
				
				//기준에 따라 변환(해당픽셀의 rgb값을 127기준으로 양극화)
				int RGB = (int)((R+G+B)/3); 
				
				//if(RGB > 127) {
				//	RGB = 0;
				//}else {
				//	RGB = 255;
				//}
				
				//흑 또는 백으로... 변환한 값 넣어주기 
				outputIMG[0][i][j] = RGB; //red
				outputIMG[1][i][j] = RGB; //green
				outputIMG[2][i][j] = RGB; //blue
			}
		}
		
		//편집중 버퍼이미지에 반영하기 
		editIMG();
		
	}
	
	//색반전
	public static void changeToRevers() {
		outputIMG = new int[3][height][width]; //공간만들고.. 
		
		//인풋메모리 참조, 기준 따라 변형하여 아웃풋메모리에 넣기 
		for(int RGB = 0; RGB < 3; RGB++) {
			for(int i = 0; i < height; i++) {
				for(int j = 0; j < width; j++) {
					outputIMG[RGB][i][j] = 255 - savedIMG[RGB][i][j];
				}
			}
		}
		
		//편집중 버퍼이미지에 반영하기 
		editIMG();
		
	}
	

	
	
	//(범용) 최초, 원본이미지 분해해서 메모리에 저장
	public static void getMemories() {
		//원본이미지 값 저장? 
		inputIMG = new int[3][height][width]; //메모리할당
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				int RGB = ORIGIN.getRGB(i, j); //해당 픽셀의 rgb값 얻어와서
				inputIMG[0][i][j] = (RGB >> 16)& 0xFF; //red
				inputIMG[1][i][j] = (RGB >> 8)& 0xFF; //green
				inputIMG[2][i][j] = (RGB)& 0xFF; //blue
			}
		}
		outputIMG = inputIMG;
		savedIMG = inputIMG;
		
	}

	//(범용)편집중이미지에다가 메모리에 저장된 내용 반영
	public static void editIMG() {
		//변환한 메모리 적용
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				
				int R = outputIMG[0][i][j]; //red 
				int G = outputIMG[1][i][j]; //green
				int B = outputIMG[2][i][j]; //blue
				int px = 0;
				
				//근데이게뭐지
				px = px | (R <<16);
				px = px | (G <<8);
				px = px | (B);
				EDITED.setRGB(i, j, px);
			}
		}
		e = new JLabel(new ImageIcon(EDITED));
		//야매 F5
		Main.up.show(Main.canvas, "origin");
		Main.up.show(Main.canvas, "edited");
		
	}
	
	// (범용) 중간저장하기
	public static void apply() {
		savedIMG = outputIMG;
	}

	
}

