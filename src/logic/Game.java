package logic;

import graphic.Game_window;

public class Game {
	
	private int height = 20;	// cima <---> baixo
	private int width = 20;		// esquerda <--> direita
	private int size;		// tamanho das barras
	private int pair;			// se o tamanho for par ent�o essa variavel � -1
	
	private int matrix[][];		// matriz do jogo // primeiro valor � a altura(y) e o segundo a largura(x)
	
	private boolean player2;		// vai ter um segundo jogador ou vai ser computador
	private boolean wait_p1;		// se deve esperar a bola bater no p1 para come�ar a agir
	private int ai_level;			// nivel de dificuldade do computador
	
	private int center_p1;			// centro da barra do P1
	private int center_p2;			// centro da barra do P2
	private int score_p1;			// pontua��o do p1
	private int score_p2;			// pontua��o do p2
	
	private int ball_position_x;	// posicao x da bola
	private int ball_position_y;	// posicao y da bola
	private int ball_move_x;		// velocidade em x // -1 esquerda <---> 1 direita 
	private int ball_move_y;		// velocidade em y // -1 cima <---> 1 baixo
	private int ball_speed;			// velocidade com que a bola se move
	
	private Keys keyboard;		// classe responsavel por pegar as teclas
	
	private Game_window window;	// janela respons�vel por exibir para o usu�rio o jogo
	
	public Game() {
		matrix = new int[height][width];
		keyboard = new Keys(this); 
		window = new Game_window(this, keyboard);
		
		size = 2;
		
		player2 = false;
		wait_p1 = true;
		ai_level = 3;
		
		center_p1 = size/2;
		center_p2 = size/2;
		score_p1 = 0;
		score_p2 = 0;
		
		ball_position_x = width/2;
		ball_position_y = 0;
		ball_move_x = 1;
		ball_move_y = 1;
		ball_speed = 3;

		pair = size%2 == 1 ? 0 : -1;
		
		update_game();
	}

	public void print_game() {
		
		for(int j=0; j < height; j++) {
			for(int i=0; i < width; i++) {
				System.out.print("[" + matrix[j][i] + "]");
			}
			System.out.print("\n");
		}
	}
	
	//////////////// GAME /////////////////////
	
	public void start_game() {
		
		new Thread() {

			@Override
			public void run() {
				while(true) {
					update_game();
				}
			}

		}.start();

		new Thread() {

			@Override
			public void run() {
				while(true) {
					move_ball();
				}
			}

		}.start();
		
		new Thread() {

			@Override
			public void run() {
				super.run();
				while(true) {
					ai_player();
				}
			}

		}.start();
		
	}
	
	public void update_game() {
		
		int start_p1;
		int end_p1;
		
		int start_p2;
		int end_p2;
		
		start_p1 = center_p1 - size/2;
		end_p1 = center_p1 + size/2 + pair;

		start_p2 = center_p2 - size/2;
		end_p2 = center_p2 + size/2 + pair;
		
		for(int j=0; j < height; j++) {
			for(int i=0; i < width; i++) {
				
				if( (i == 0 && j >= start_p1 && j <= end_p1) || (i == width-1 && j >= start_p2 && j <= end_p2))
					matrix[j][i] = 1;
				else
					matrix[j][i] = 0;
			}
		}
		
		matrix[ball_position_y][ball_position_x] = 2;
		
		window.paint(null);
		//print_game();
	}
	
	/////////////////// BALL ////////////////////
	
	public void move_ball() {

		try {
			Thread.sleep(1000/ball_speed);
		} catch (InterruptedException e) {
			System.out.println(">> IllegalArgumentException - if the value of millis is negative");
			System.out.println(">> InterruptedException - if any thread has interrupted the current thread. The interrupted status of the current thread is cleared when this exception is thrown");
			e.printStackTrace();
		}

		// bola passou dos jogadores
		if(ball_position_x + ball_move_x < 0) {

			ball_position_x = width/2;
			ball_position_y = 0;

			ball_move_x = 1;
			ball_move_y = 1;

			score_p2++;
			
			return;
		} else if (ball_position_x + ball_move_x > width-1) {

			ball_position_x = width/2 - 1;
			ball_position_y = 0;

			ball_move_x = -1;
			ball_move_y = 1;

			score_p1++;
			
			return;
		}

		window.setTitle(score_p1 + "x" + score_p2);

		// Se bola encostou no teto ou ch�o inverte a dire��o y
		if(ball_position_y + ball_move_y < 0 || ball_position_y + ball_move_y >= height)
			ball_move_y *= -1; 

		// Bola encontrando outro jogador
		int start = center_p1 - size/2;
		int end = center_p1 + size/2 + pair;
		if(ball_position_x + ball_move_x ==  0 &&
				ball_position_y + ball_move_y >= start &&
				ball_position_y + ball_move_y <= end)
			ball_move_x *= -1;

		start = center_p2 - size/2;
		end = center_p2 + size/2 + pair;
		if(ball_position_x + ball_move_x == width-1 &&
				ball_position_y + ball_move_y >= start &&
				ball_position_y + ball_move_y <= end)
			ball_move_x *= -1;

		// Nova posi��o da bola
		ball_position_x += ball_move_x;
		ball_position_y += ball_move_y;


	}
	
	/////////////////// PLAYERS ///////////////////////
	
	public void move_up_p1() {
		
		if( (center_p1 - size/2) - 1 < 0)
			return;
		
		center_p1--;
		
	}
	
	public void move_down_p1() {
		
		if( (center_p1 + size/2 + pair) + 1 >= height )
			return;
		
		center_p1++;
		
	}
	
	public void move_up_p2() {
		
		if( (center_p2 - size/2) - 1 < 0)
			return;
		
		center_p2--;
		
	}
	
	public void move_down_p2() {
		
		if( (center_p2 + size/2 + pair) + 1 >= height )
			return;
		
		center_p2++;
		
	}
	
	public void reset_score() {
		score_p1 = 0;
		score_p2 = 0;
	}
	
	///////////////////// AI PLAYER ///////////////////////
	
	public void ai_player() {
		
		if(player2 == true)
			return;
		
		try {
			Thread.sleep(1000/ai_level);
		} catch (InterruptedException e) {
			System.out.println(">> IllegalArgumentException - if the value of millis is negative");
			System.out.println(">> InterruptedException - if any thread has interrupted the current thread. The interrupted status of the current thread is cleared when this exception is thrown");
			e.printStackTrace();
		}

		if(wait_p1 == true && ball_move_x == -1)
			return;
		
		int ball_move_y = this.ball_move_y;

		// Se bola encostou no teto ou ch�o inverte a dire��o y
		if(ball_position_y + ball_move_y < 0 || ball_position_y + ball_move_y >= height)
			ball_move_y *= -1; 
		
		// Lembrando que coordenada y aumenta quando a bola vai para baixo no jogo
		if(ball_position_y + ball_move_y > center_p2)
			move_down_p2();
		else if(ball_position_y + ball_move_y < center_p2)
			move_up_p2();
		
	}
	
	///////////////////// GET /////////////////////////////


	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public int[][] getMatrix() {
		return matrix;
	}

	public int getScore_p1() {
		return score_p1;
	}

	public int getScore_p2() {
		return score_p2;
	}
	
}
