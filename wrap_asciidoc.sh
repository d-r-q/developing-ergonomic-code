#!/bin/bash
script_dir=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/bin

mkdir -p ${script_dir}

if [ ! -x ${script_dir}/asciidoctor ]; then
    echo "#!/bin/bash
$(which asciidoctor) -r asciidoctor-html5s -b html5s -r asciidoctor-diagram \"\$@\"
" > ${script_dir}/asciidoctor

    chmod +x bin/asciidoctor
fi
