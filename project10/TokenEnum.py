from enum import Enum

class TokenType(Enum):
    KEYWORD = "KEYWORD"
    SYMBOL = "SYMBOL"
    INT_CONST = "INT_CONST"
    STRING_CONST = "STRING_CONST"
    IDENTIFIER = "IDENTIFIER"