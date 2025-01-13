import sys
import os
import argparse
from CompilationEngine import CompilationEngine


class JackCompiler:
    def __init__(self, input_path):
        self.input_path = input_path

    def get_files(self):  # Collect .jack files from a file or directory
        if os.path.isfile(self.input_path) and self.input_path.endswith(".jack"):
            return [self.input_path]

        elif os.path.isdir(self.input_path):
            return [
                os.path.join(self.input_path, name)
                for name in os.listdir(self.input_path)
                if name.endswith(".jack") and os.path.isfile(os.path.join(self.input_path, name))
            ]

        else:
            raise FileNotFoundError(f"Invalid path: '{self.input_path}'")

    @staticmethod
    def output_file(jack_file):  # Generate .xml output file name
        return jack_file.replace(".jack", ".xml")

    def compile(self):  # Compile each .jack file
        files = self.get_files()

        if not files:
            print(f"No .jack files found in '{self.input_path}'.")
            return

        for jack_file in files:
            xml_file = self.output_file(jack_file)
            engine = CompilationEngine(jack_file, xml_file)
            engine.compile_class()


def main():
    path = sys.argv[1] if len(sys.argv) > 1 else None
    if not path:
        print("Usage: python script.py <path_to_jack_file_or_directory>")
        sys.exit(1)

    try:
        compiler = JackCompiler(path)
        compiler.compile()
    except FileNotFoundError as e:
        print(f"Error: {e}")
    except Exception as e:
        print(f"Unexpected error: {e}")
        sys.exit(1)



if __name__ == "__main__":
    main()
