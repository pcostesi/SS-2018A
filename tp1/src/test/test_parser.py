import unittest
from parser import parse

class TestParserMethods(unittest.TestCase):
    def test_parse_sample_file(self):
        generations = parse("test/input.txt")
        t0, p0 = next(generations)
        self.assertEqual(t0, 0)
        self.assertEqual(p0[0], (1, 2, 3.5, 6.4))


if __name__ == '__main__':
    unittest.main()
