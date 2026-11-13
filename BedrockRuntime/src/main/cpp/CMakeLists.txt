cmake_minimum_required(VERSION 3.22)
project(bedroom_levi LANGUAGES C CXX)

add_subdirectory(
    "../../../../LeviLaunchroid/app/src/main/cpp"
    "${CMAKE_CURRENT_BINARY_DIR}/levi"
)
