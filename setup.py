from setuptools import setup
from install import *

setup(
    name="pyja",
    version="0.1",
    author="Jaime A. Pavlich-Mariscal",
    author_email="jaime.pavlich@gmail.com",
    packages=["pyja"],
    install_requires=["jpype1",],
    include_package_data=True,
    license="MIT",
    description="Analyze java code. Uses javaparser to analyze the code and automatically processess maven dependencies to find all required sources and jars",
    cmdclass={"install": Install, "develop": Develop},
)
