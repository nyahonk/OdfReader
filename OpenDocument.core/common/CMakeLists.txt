add_library(odr_common STATIC
        src/Constants.cpp
        src/Html.cpp
        src/StringUtil.cpp
        src/TableCursor.cpp
        src/TablePosition.cpp
        src/TableRange.cpp
        src/XmlUtil.cpp
        )
target_include_directories(odr_common PUBLIC include)
target_link_libraries(odr_common
        PUBLIC
        tinyxml2

        odr_access

        odr-interface
        )
set_property(TARGET odr_common PROPERTY POSITION_INDEPENDENT_CODE ON)
