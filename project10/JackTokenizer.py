import re
from TokenEnum import TokenType
class JackTokenizer:
    # Token Types
    TOKEN_TYPES = {
        TokenType.KEYWORD: r"^\s*(class|constructor|function|method|static|field|var|int|char|boolean|void|true|false|null|this|let|do|if|else|while|return)\s*",
        TokenType.SYMBOL: r"^\s*([{}()\[\].,;+\-*/&|<>=~])\s*",
        TokenType.INT_CONST: r"^\s*(\d+)\s*",
        TokenType.STRING_CONST: r'^\s*"(.*?)"\s*',
        TokenType.IDENTIFIER: r"^\s*([a-zA-Z_][a-zA-Z0-9_]*)\s*"
    }
    COMMENT_PATTERN = r"(//.*)|(/\*([^*]|[\r\n]|(\*+([^*/]|[\r\n])))*\*+/)"
    WHITESPACE_PATTERN = r"^\s*$"

    def __init__(self, input_path):
        with open(input_path, "r") as file:
            self.source = file.read()
        self._remove_comments()
        self._token_type = None
        self._current_token = None

    def advance(self):
        """Advance to the next token."""
        if not self.has_more_tokens():
            return

        for token_type, pattern in self.TOKEN_TYPES.items():
            match = re.match(pattern, self.source)
            if match:
                self.source = self.source[len(match.group(0)):]
                self._token_type = token_type
                self._current_token = match.group(1)
                return
        # Skip unknown characters if no match is found
        self.source = self.source[1:]

    def _remove_comments(self) -> None: # Remove comments from the source
        self.source = re.sub(self.COMMENT_PATTERN, "", self.source)

    def has_more_tokens(self) -> bool : # Check if there are more tokens in the source
        return not re.fullmatch(self.WHITESPACE_PATTERN, self.source)
    @property
    def token_type(self): # Return the type of the current token
        return self._token_type

    @property
    def current_token(self):# Return the keyword value of the current token
        return self._current_token