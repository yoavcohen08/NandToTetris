from JackTokenizer import JackTokenizer
from TokenEnum  import TokenType

# Operators recognized in expressions
BINARY_OPS = {"+", "-", "*", "/", "&", "|", "<", ">", "="}

class CompilationEngine:
    # Initializes with input and output files
    def __init__(self, input_file, output_file):
        self.tokenizer = JackTokenizer(input_file)
        self.output = open(output_file, "w", encoding="utf-8")
        self.indent = 0

    # Compiles a class
    def compile_class(self):
        self.tokenizer.advance()
        self._open_tag("class")
        self._write_keyword()
        self.tokenizer.advance()
        self._writeID_and_advance_symbol()
        self._write_and_advance_symbol()
        while self.tokenizer.token_type == TokenType.KEYWORD and self.tokenizer.current_token in ("static", "field"):
            self.compile_var_dec()
        while self.tokenizer.token_type ==  TokenType.KEYWORD and self.tokenizer.current_token in ("constructor", "function", "method"):
            self.compile_subroutine()

        self._write_symbol()
        self._close_tag("class")
        self.output.close()


    def compile_var_dec(self): # Compiles variable declarations
        tag_name = "classVarDec" if self.tokenizer.current_token in ("static", "field") else "varDec"
        # Open the appropriate tag
        self._open_tag(tag_name)
        # Write the keyword ('static', 'field', or 'var')
        self._write_keyword()
        self.tokenizer.advance()
        # Compile type and variable names
        self._compile_type_vars()
        # Close the appropriate tag
        self._close_tag(tag_name)

    # Compiles subroutines
    def compile_subroutine(self):
        self._open_tag("subroutineDec")
        self._write_keyword()
        self.tokenizer.advance()

        if self.tokenizer.token_type == TokenType.KEYWORD:
            self._write_keyword()
        else:
            self._write_identifier()
        self.tokenizer.advance()

        self._writeID_and_advance_symbol()
        self._write_and_advance_symbol()
        self.compile_params()
        self._write_and_advance_symbol()
        self.compile_body()
        self._close_tag("subroutineDec")
        self.tokenizer.advance()

    # Compiles parameter list
    def compile_params(self):
        self._open_tag("parameterList")
        while not (self.tokenizer.token_type == TokenType.SYMBOL and self.tokenizer.current_token == ")"):
            self._write_type_or_identifier()
            self.tokenizer.advance()
            self._writeID_and_advance_symbol()

            if self.tokenizer.token_type == TokenType.SYMBOL and self.tokenizer.current_token == ",":
                self._write_and_advance_symbol()
        self._close_tag("parameterList")

    # Compiles subroutine body
    def compile_body(self):
        self._open_tag("subroutineBody")
        self._write_and_advance_symbol()

        while self.tokenizer.token_type == TokenType.KEYWORD  and self.tokenizer.current_token == "var":
            self.compile_var_dec()

        self.compile_statements()
        self._write_symbol()
        self._close_tag("subroutineBody")

    # Compiles statements
    def compile_statements(self):
        self._open_tag("statements")
        while self.tokenizer.token_type == TokenType.KEYWORD:
            kw = self.tokenizer.current_token
            self._compile_statement_by_keyword(kw)
        self._close_tag("statements")

    def compile_let(self):  # Compiles let statement
        self._open_tag("letStatement")
        self._write_keyword()
        self.tokenizer.advance()
        self._writeID_and_advance_symbol()

        if self.tokenizer.token_type == TokenType.SYMBOL and self.tokenizer.current_token == "[":
            self._write_and_advance_symbol()
            self.compile_expr()
            self._write_and_advance_symbol()

        self._write_and_advance_symbol()
        self.compile_expr()
        self._write_symbol()
        self._close_tag("letStatement")
        self.tokenizer.advance()

    def compile_if(self): # Compiles if statement
        self._open_tag("ifStatement")
        self._write_keyword()
        self.tokenizer.advance()
        self._write_and_advance_symbol()
        self.compile_expr()
        self._write_and_advance_symbol()
        self._write_and_advance_symbol()
        self.compile_statements()
        self._write_and_advance_symbol()

        if self.tokenizer.token_type == TokenType.KEYWORD  and self.tokenizer.current_token == "else":
            self._write_keyword()
            self.tokenizer.advance()
            self._write_and_advance_symbol()
            self.compile_statements()
            self._write_and_advance_symbol()

        self._close_tag("ifStatement")

    # Compiles while statement
    def compile_while(self):
        self._open_tag("whileStatement")
        self._write_keyword()
        self.tokenizer.advance()
        self._write_and_advance_symbol()
        self.compile_expr()
        self._write_and_advance_symbol()
        self._write_and_advance_symbol()
        self.compile_statements()
        self._write_symbol()
        self._close_tag("whileStatement")
        self.tokenizer.advance()

    def compile_do(self): # Compiles do statement
        self._open_tag("doStatement")
        self._write_keyword()
        self.tokenizer.advance()
        self._writeID_and_advance_symbol()

        if self.tokenizer.token_type == TokenType.SYMBOL and self.tokenizer.current_token == ".":
            self._write_and_advance_symbol()
            self._writeID_and_advance_symbol()

        self._write_and_advance_symbol()
        self.compile_expr_list()
        self._write_and_advance_symbol()
        self._write_symbol()
        self._close_tag("doStatement")
        self.tokenizer.advance()

    def compile_return(self): # Compiles return statement
        self._open_tag("returnStatement")
        self._write_keyword()
        self.tokenizer.advance()
        if not (self.tokenizer.token_type == TokenType.SYMBOL and self.tokenizer.current_token == ";"):
            self.compile_expr()
        self._write_symbol()
        self._close_tag("returnStatement")
        self.tokenizer.advance()

    def compile_expr(self): # Compiles expressions
        self._open_tag("expression")
        self.compile_term()
        while self.tokenizer.token_type == TokenType.SYMBOL and self.tokenizer.current_token in BINARY_OPS:
            self._write_and_advance_symbol()
            self.compile_term()
        self._close_tag("expression")

    def compile_term(self): # Compiles terms
        self._open_tag("term")
        tok_type = self.tokenizer.token_type
        if tok_type == TokenType.INT_CONST:
            self._write("integerConstant", self.tokenizer.current_token)
            self.tokenizer.advance()
        elif tok_type == TokenType.STRING_CONST:
            self._write("stringConstant", self.tokenizer.current_token)
            self.tokenizer.advance()
        elif tok_type == TokenType.KEYWORD:
            self._write_keyword()
            self.tokenizer.advance()
        elif tok_type == TokenType.IDENTIFIER:
            self._writeID_and_advance_symbol()
            self._handle_complex_term()
        elif tok_type == TokenType.SYMBOL and self.tokenizer.current_token == "(":
            self._write_and_advance_symbol()
            self.compile_expr()
            self._write_and_advance_symbol()
        elif tok_type == TokenType.SYMBOL and self.tokenizer.current_token in ("-", "~"):
            self._write_and_advance_symbol()
            self.compile_term()
        self._close_tag("term")

    def compile_expr_list(self):  # Compiles expression list
        self._open_tag("expressionList")
        if not (self.tokenizer.token_type == TokenType.SYMBOL and self.tokenizer.current_token == ")"):
            self.compile_expr()
            while self.tokenizer.token_type == TokenType.SYMBOL and self.tokenizer.current_token == ",":
                self._write_and_advance_symbol()
                self.compile_expr()
        self._close_tag("expressionList")

    def _compile_type_vars(self): # Common: type and vars
        self._write_type_or_identifier()
        self.tokenizer.advance()

        self._writeID_and_advance_symbol()

        while self.tokenizer.token_type == TokenType.SYMBOL and self.tokenizer.current_token == ",":
            self._write_and_advance_symbol()
            self._writeID_and_advance_symbol()

        self._write_and_advance_symbol()

    def _open_tag(self, tag):  # Helpers: Write XML
        self.output.write("  " * self.indent + f"<{tag}>\n")
        self.indent += 1

    def _close_tag(self, tag):
        self.indent -= 1
        self.output.write("  " * self.indent + f"</{tag}>\n")

    def _write(self, tag, text):
        self.output.write("  " * self.indent + f"<{tag}> {text} </{tag}>\n")

    def _write_keyword(self):
        self._write("keyword", self.tokenizer.current_token)

    def _write_identifier(self):
        self._write("identifier", self.tokenizer.current_token)

    def _write_symbol(self):
        sym = self.tokenizer.current_token
        if sym == "<":
            sym = "&lt;"
        elif sym == ">":
            sym = "&gt;"
        elif sym == "&":
            sym = "&amp;"
        self._write("symbol", sym)

    def _write_type_or_identifier(self):
        if self.tokenizer.token_type == TokenType.KEYWORD:
            self._write_keyword()
        else:
            self._write_identifier()

    def _handle_complex_term(self):
        if self.tokenizer.token_type == TokenType.SYMBOL:
            sym = self.tokenizer.current_token
            if sym == "[":
                self._write_and_advance_symbol()
                self.compile_expr()
                self._write_and_advance_symbol()
            elif sym == ".":
                self._write_and_advance_symbol()
                self._writeID_and_advance_symbol()
                self._write_and_advance_symbol()
                self.compile_expr_list()
                self._write_and_advance_symbol()
            elif sym == "(":
                self._write_and_advance_symbol()
                self.compile_expr_list()
                self._write_and_advance_symbol()

    def _compile_statement_by_keyword(self, kw):
        if kw == "let":
            self.compile_let()
        elif kw == "if":
            self.compile_if()
        elif kw == "while":
            self.compile_while()
        elif kw == "do":
            self.compile_do()
        elif kw == "return":
            self.compile_return()

    def _write_and_advance_symbol(self): # Helpers: Write symbol and advance
        self._write_symbol()
        self.tokenizer.advance()

    def _writeID_and_advance_symbol(self):  # Helpers: Write identifier and advance
        self._write_identifier()
        self.tokenizer.advance()

