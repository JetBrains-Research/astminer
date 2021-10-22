from setuptools import setup

setup(
    name='javalang_astminer_wrapper',
    version='1.0.0',
    description='Astminer wrapper for javalang parser',
    packages=['aw_javalang'],
    license='MIT',
    author='Ilya Utkin',
    entry_points={
        'console_scripts': ['aw_javalang = aw_javalang.main:main']
    },
    install_requires=[
        'javalang~=0.13.0'
    ]
)
