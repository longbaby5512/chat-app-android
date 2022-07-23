cmake_minimum_required(VERSION 3.18.1)


project("chaotic")


add_library(chaotic SHARED chaotic.cpp src/Hash.cpp src/Utils.cpp src/ChaoticMap.cpp src/ChaoticCypher.cpp)
find_library(log-lib log)
target_include_directories(chaotic PUBLIC "${PROJECT_SOURCE_DIR}/include")
target_link_libraries(chaotic ${log-lib})