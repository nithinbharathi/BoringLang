import java.util.*;

public class Application {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		while(true) {
			System.out.print("> ");
			String line = scanner.nextLine();
			
			if(line == null || line.length() == 0)
				return;
			Lexer lexer = new Lexer(line);
			SyntaxToken token;
			
			while((token = lexer.nextToken()).getKind() != SyntaxKind.EOFToken) {
				System.out.println(token.getKind()+" "+token.getText());
			}
		}
		
		//scanner.close();
	}
}
enum SyntaxKind{
	NumberToken,
	WhiteSpaceToken,
	PlusToken,
	MinusToken,
	StarToken,
	SlashToken,
	OpenParanthesisToken,
	CloseParanthesisToken,
	BadToken,
	EOFToken,
	NumberExpression,
	BinaryExpression
}
class SyntaxToken{
	private SyntaxKind kind;
	private int position;
	private String text;
	private Object value;
	
	public SyntaxToken(SyntaxKind kind, int position, String text, Object value) {
		this.kind = kind;
		this.position = position;
		this.text = text;
		this.value = value;
	}

	public SyntaxKind getKind() {
		return kind;
	}

	public void setKind(SyntaxKind kind) {
		this.kind = kind;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	
}

class Lexer{
	private static String text;
	private int position;
	
	public Lexer(String text) {
		this.text = text;
	}
	
	private char getCurrent() {
		return position>=text.length()?'\0':text.charAt(position);
	}
	
	private void next() {
		this.position++;
	}
	
	public SyntaxToken nextToken() {
		if(position>=text.length())
			return new SyntaxToken(SyntaxKind.EOFToken, position, "\0", null);
		
		if(Character.isDigit(getCurrent())) {
			int start = position;
			
			while(Character.isDigit(getCurrent())) {
				next();
			}
			
			String substring = text.substring(start, position);
			int number = Integer.parseInt(substring);
			
			return new SyntaxToken(SyntaxKind.NumberToken, start, substring, number);
		}
		
		if(Character.isWhitespace(getCurrent())) {
			int start = position;
			
			while(Character.isWhitespace(getCurrent())) {
				next();
			}
			
			String substring = text.substring(start, position);
			
			return new SyntaxToken(SyntaxKind.WhiteSpaceToken, start, substring, null);
		}
		
		if(getCurrent() == '+') {
			next();
			return new SyntaxToken(SyntaxKind.PlusToken, position-1, "+", null);
		}else if(getCurrent() == '-') {
			next();
			return new SyntaxToken(SyntaxKind.MinusToken, position-1, "-", null);
		}else if(getCurrent() == '*') {
			next();
			return new SyntaxToken(SyntaxKind.StarToken, position-1, "*", null);
		}else if(getCurrent() == '(') {
			next();
			return new SyntaxToken(SyntaxKind.OpenParanthesisToken, position-1, "(", null);
		}else if(getCurrent() == ')') {
			next();
			return new SyntaxToken(SyntaxKind.CloseParanthesisToken, position-1, ")", null);
		}else if(getCurrent() == '/') {
			next();
			return new SyntaxToken(SyntaxKind.SlashToken, position-1, "/", null);
		}
		
		next();
		return new SyntaxToken(SyntaxKind.BadToken, position-1, text.substring(position-1,position), null);


	}
}

class Parser{
	private int position;
	private int size = 0;
	private SyntaxToken tokens[];
	
	public Parser(String text) {	
		SyntaxToken token;
		Lexer lexer = new Lexer(text);

		while((token = lexer.nextToken()).getKind() != SyntaxKind.EOFToken) {
			if(token.getKind() != SyntaxKind.BadToken && token.getKind() != SyntaxKind.WhiteSpaceToken) {
				tokens[size++] = token;
			}
		}
	}
	
	public SyntaxToken peek(int offset) {
		if(offset>=size)
			return tokens[size-1];
		
		return tokens[offset];
	}
	
	private SyntaxToken current() {
		return peek(0);
	}
}

abstract class SyntaxNode{
	public abstract SyntaxKind kind();
}

abstract class ExpressionSyntax extends SyntaxNode{
	
}

class NumberSyntax extends ExpressionSyntax{

	private SyntaxToken numberToken;
	public NumberSyntax(SyntaxToken numberToken) {
		this.numberToken = numberToken;
	}
	
	@Override
	public SyntaxKind kind() {
		return SyntaxKind.NumberExpression;
	}
	
}

class BinaryExpressionSyntax extends ExpressionSyntax{
	private ExpressionSyntax left, right;
	private SyntaxNode operatorToken;
	
	public BinaryExpressionSyntax(ExpressionSyntax left, ExpressionSyntax right, SyntaxNode operatorToken) {
		this.left = left;
		this.right = right;
		this.operatorToken = operatorToken;
	}

	public ExpressionSyntax getLeft() {
		return left;
	}

	public void setLeft(ExpressionSyntax left) {
		this.left = left;
	}

	public ExpressionSyntax getRight() {
		return right;
	}

	public void setRight(ExpressionSyntax right) {
		this.right = right;
	}

	public SyntaxNode getOperatorToken() {
		return operatorToken;
	}

	public void setOperatorToken(SyntaxNode operatorToken) {
		this.operatorToken = operatorToken;
	}

	@Override
	public SyntaxKind kind() {
		return SyntaxKind.BinaryExpression;
	}
}
